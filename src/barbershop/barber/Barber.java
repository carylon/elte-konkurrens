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
                while (barber.barberShop.getFinallyClosed() == false) {
                    try {
                        Client c = barber.barberShop.getNextClient();
                        if (c == null) {
                            // If there is no client in the shop, wait for the next signal.
                            System.out.println("Client list is empty");
                            barber.wait(BarberShop.DAY_LENGTH);
                        } else {
                            System.out.println("Client: " + c.getName());
                        }
                        while (c != null) {
                            barber.client = c;
                            Random rand = new Random();
                            int serviceTime = rand.nextInt(MAX_SERVICE_TIME - MIN_SERVICE_TIME) + MIN_SERVICE_TIME;
                            Thread.sleep(rand.nextInt(MAX_SERVICE_TIME - MIN_SERVICE_TIME) + MIN_SERVICE_TIME);
                            barber.barberShop.barberFinishedWithAClient(barber, barber.client, serviceTime);
                            barber.client = null;
                            c = barber.barberShop.getNextClient();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            System.out.println("Barber thread finished.");
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