import java.util.Date;
import java.util.Random;

import barbershop.BarberShop;
import barbershop.BarberShopOutOfServiceException;
import barbershop.BarberShopReachedMaxCapacity;
import client.Client;

class Main {
    public static void main(String[] args) {
        final BarberShop bs = new BarberShop();

        // Wait until 0:00
        try {
            Date d = new Date();
            Thread.sleep(BarberShop.DAY_LENGTH - (d.getTime() % BarberShop.DAY_LENGTH));
        } catch (Exception e) {
            System.out.println("Sleep failed");
        }

        // Start service for 5 days.
        bs.startService(5);

        // Start The barber shop service
        int i = 0;
        while (!bs.getFinallyClosed()) {
            try {
                Random rand = new Random();
                Thread.sleep(rand.nextInt(250));
            } catch (Exception e) {
                System.out.println("Sleep failed");
            }
            try {
                bs.addClient(new Client("Client - " + i));
                i++;
            } catch (BarberShopReachedMaxCapacity e) {
                // Do nothing for now
                System.out.println(e);
            } catch (BarberShopOutOfServiceException e) {
                // Do nothing for now
                System.out.println(e);
            }
        }
        System.out.println("Main process finished");
    }
}