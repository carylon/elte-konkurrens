package barbershop;

import java.util.Date;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

import client.Client;
import barbershop.barber.Barber;

public class BarberShop {
    private static final short MAX_CLIENT = 5;
    public static final short DAY_LENGTH = 9600;
    public static final short SERVICE_START_TIME = 3200;
    public static final short SERVICE_END_TIME = 6400;

    private boolean finallyClosed;
    private final LinkedList<Client> clients;
    private final Barber barber;

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
        this.barber = new Barber(this);

        new Thread(() -> {
            synchronized (this.clients) {                    
                while(!this.finallyClosed || (this.finallyClosed && !this.clients.isEmpty())) {
                    try {
                        if (this.clients.isEmpty()) this.clients.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    if (!this.clients.isEmpty()) {
                        final Client c = this.clients.poll();
                        this.barber.addClient(c);
                    }
                }
            }
        }).start();
    }

    public void barberFinished() {
        this.servedClients++;
        if (!this.clients.isEmpty()) {
            synchronized (this.clients) {
                this.clients.notify();
            }
        }
    }

    public void newClientArrived(Client c) throws BarberShopOutOfServiceException, BarberShopReachedMaxCapacity {
        if (this.clients.size() >= MAX_CLIENT) {
            this.skippedClientBecauseOfMaxCapacity++;
            throw new BarberShopReachedMaxCapacity(MAX_CLIENT);
        }
        final var time = new Date().getTime() % DAY_LENGTH;
        if (time < SERVICE_START_TIME || time > SERVICE_END_TIME) {
            this.skippedClientBecauseOfClosingHours++;
            throw new BarberShopOutOfServiceException();
        }

        this.clients.addLast(c);
        if (!this.barber.hasClient()) {
            synchronized (this.clients) {
                this.clients.notify();
            }
        }
    }
    
    public void closeShop() {
        this.finallyClosed = true;
        synchronized (this.clients) {
            this.clients.notify();
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
