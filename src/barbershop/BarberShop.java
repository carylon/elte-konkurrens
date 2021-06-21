package barbershop;

import java.util.Date;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

import client.Client;
import barbershop.barber.Barber;

public class BarberShop {
    private static final short BARBERS_COUNT = 2;
    private static final short MAX_CLIENT = 5;
    public static final short DAY_LENGTH = 9600;
    public static final short SERVICE_START_TIME = 3200;
    public static final short SERVICE_END_TIME = 6400;

    private boolean finallyClosed;
    private final LinkedList<Client> clients;
    private final LinkedList<Barber> barbers;

    // Statistics
    private int skippedClientBecauseOfMaxCapacity;
    private int skippedClientBecauseOfClosingHours;
    private int servedClients;

    public BarberShop() {
        this.skippedClientBecauseOfMaxCapacity = 0;
        this.skippedClientBecauseOfClosingHours = 0;
        this.servedClients = 0;

        this.finallyClosed = false;
        this.clients = new LinkedList<>();
        this.barbers = new LinkedList<>();
        for (var i = 0; i < BARBERS_COUNT; i++) {
            this.barbers.add(new Barber("Barber " + Integer.toString(i), this));
        }

        this.barbers.forEach(barber -> new Thread(() -> {
            while(!this.finallyClosed || (this.finallyClosed && !this.clients.isEmpty())) {
                Client c = null;
                synchronized (this.clients) {
                    if (this.clients.isEmpty()) {
                        try {
                            this.clients.wait();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }
                    c = this.clients.poll();
                }
                if (c != null) barber.addClient(c);
            }
        }).start());
    }

    public void barberFinished(Barber barber, Client client) {
        System.out.println("Barber: " + barber.getName() + " finished with client: " + client.getName());
        this.servedClients++;
        synchronized (this.clients) {
            if (!this.clients.isEmpty()) {
                this.clients.notifyAll();
            }
        }
    }

    public void newClientArrived(Client client) throws BarberShopOutOfServiceException, BarberShopReachedMaxCapacity {
        synchronized (this.clients) {
            System.out.println("New client arrived to the barber shop: " + client.getName());
            final var time = new Date().getTime() % DAY_LENGTH;
            if (time < SERVICE_START_TIME || time > SERVICE_END_TIME) {
                this.skippedClientBecauseOfClosingHours++;
                throw new BarberShopOutOfServiceException();
            }
            if (this.clients.size() >= MAX_CLIENT) {
                this.skippedClientBecauseOfMaxCapacity++;
                throw new BarberShopReachedMaxCapacity(MAX_CLIENT);
            }
            this.clients.addLast(client);
            if (this.barbers.stream().anyMatch(barber -> !barber.hasClient())) {
                this.clients.notifyAll();
            }
        }
    }
    
    public void closeShop() {
        this.finallyClosed = true;
        synchronized (this.clients) {
            this.clients.notifyAll();
        }
    }

    public void startService(int days) {
        final var t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                BarberShop.this.closeShop();
                t.cancel();
            }
        }, (long) (DAY_LENGTH * days));
    }

    public boolean getFinallyClosed() {
        return this.finallyClosed;
    }

    public void printStatisctics() {
        System.out.println("Served Clients: " + this.servedClients);
        System.out.println("Skipped clients because of closing hours: " + this.skippedClientBecauseOfClosingHours);
        System.out.println("Skipped clients because of max capacity: " + this.skippedClientBecauseOfMaxCapacity);
    }
}
