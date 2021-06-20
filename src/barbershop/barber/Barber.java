package barbershop.barber;

import java.util.Random;

import barbershop.BarberShop;
import client.Client;

public class Barber {
    private static final short MIN_SERVICE_TIME = 20;
    private static final short MAX_SERVICE_TIME = 200;

    private final BarberShop barberShop;
    private Client client;
    
    private final Random rand;

    public Barber(BarberShop bs) {
        this.client = null;
        this.rand = new Random();
        this.barberShop = bs;
    }

    public void addClient(Client c) {
        this.client = c;
        int serviceTime = rand.nextInt(MAX_SERVICE_TIME - MIN_SERVICE_TIME) + MIN_SERVICE_TIME;
        try {
            Thread.sleep(rand.nextInt(serviceTime));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        this.client = null;
        this.barberShop.barberFinished(this);
    }

    public boolean hasClient() {
        return this.client != null;
    }
}
