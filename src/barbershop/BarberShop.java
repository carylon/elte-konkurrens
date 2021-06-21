package barbershop;

import java.util.Date;
import java.util.LinkedList;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Predicate;

import client.Client;
import client.ClientWants;
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
        this.barbers.add(new Barber("Barber 1", this, ClientWants.HAIR_ONLY));
        this.barbers.add(new Barber("Barber 2", this, ClientWants.HAIR_AND_BEARD));
        for (var i = 2; i < BARBERS_COUNT; i++) {
            this.barbers.add(new Barber("Barber " + Integer.toString(i), this, ClientWants.HAIR_ONLY));
        }

        this.barbers.forEach(barber -> new Thread(() -> {
            Predicate<Client> barberCanWorkOnClient = (Client c) -> {
                if (barber.getCanDo() == ClientWants.HAIR_AND_BEARD) return true;
                return barber.getCanDo() == c.getNeeds();
            };
            while(!this.finallyClosed || (this.finallyClosed && !this.clients.isEmpty())) {
                Optional<Client> c;
                synchronized (this.clients) {
                    boolean canWorkOnClient = !this.clients.isEmpty() && this.clients.stream().anyMatch(barberCanWorkOnClient);
                    if (!canWorkOnClient) {
                        try {
                            this.clients.wait();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }

                    c = this.clients.stream()
                        .filter(barberCanWorkOnClient)
                        .findFirst();

                    if (c.isPresent()) this.clients.remove(c.get());
                }
                if (c.isPresent()) barber.addClient(c.get());
            }
        }).start());
    }

    public void barberFinished(Barber barber, Client client) {
        System.out.println("Barber: " + barber.getName() + " finished with client: " + client.getName() +
        ( client.getNeeds() == ClientWants.HAIR_AND_BEARD ? " with Hair and beard" : " with only hair"));
        this.servedClients++;
    }

    public void newClientArrived(Client client) throws BarberShopOutOfServiceException, BarberShopReachedMaxCapacity {
        System.out.println("New client arrived to the barber shop: " + client.getName());
        final var time = new Date().getTime() % DAY_LENGTH;
        if (time < SERVICE_START_TIME || time > SERVICE_END_TIME) {
            this.skippedClientBecauseOfClosingHours++;
            throw new BarberShopOutOfServiceException();
        }
        synchronized (this.clients) {
            if (this.clients.size() >= MAX_CLIENT) {
                this.skippedClientBecauseOfMaxCapacity++;
                throw new BarberShopReachedMaxCapacity(MAX_CLIENT);
            }
            this.clients.addLast(client);
            if (this.barbers.stream().anyMatch(barber -> !barber.hasClient())) {
                this.clients.notify();
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
