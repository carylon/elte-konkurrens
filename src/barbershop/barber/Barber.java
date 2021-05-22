package barbershop.barber;

import java.util.Random;

import barbershop.BarberShop;
import client.Client;

public class Barber {
    private static final short MIN_SERVICE_TIME = 20;
    private static final short MAX_SERVICE_TIME = 200;

    private final BarberShop barberShop;
    private Client client;
    
    private Barber(BarberShop barberShop) {
        this.barberShop = barberShop;
    }

    public static final Barber createBarber(BarberShop barberShop) {
        final Barber barber = new Barber(barberShop);

        Thread thread = new Thread(() -> {
            synchronized (barber) {
                while(barber.barberShop.getFinallyClosed() == false) {
                    try {
                        barber.wait(BarberShop.DAY_LENGTH);
                        if (barber.client != null) {
                            Random rand = new Random();
                            Thread.sleep(rand.nextInt(MAX_SERVICE_TIME - MIN_SERVICE_TIME) + MIN_SERVICE_TIME);
                            barber.client = null;
                            barber.barberShop.notify();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        thread.start();

        return barber;
    }

    public void addClient(Client c) throws BarberHasClientException {
        if (this.client != null) throw new BarberHasClientException();
        if (c == null) throw new NullPointerException();
        this.client = c;
    }
}