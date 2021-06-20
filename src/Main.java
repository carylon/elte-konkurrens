
import java.util.Date;
import java.util.Random;

import barbershop.BarberShop;
import barbershop.BarberShopOutOfServiceException;
import barbershop.BarberShopReachedMaxCapacity;
import client.Client;

public class Main {
    public static void main(String[] args) {
        final var barberShop = new BarberShop();
        // Wait until 0:00
        try {
            var d = new Date();
            Thread.sleep(BarberShop.DAY_LENGTH - (d.getTime() % BarberShop.DAY_LENGTH));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Start service for 5 days.
        barberShop.startService(5);

        // Start The barber shop service
        var i = 0;
        while (!barberShop.getFinallyClosed()) {
            try {
                Thread.sleep(Main.clientRandomArriveTimeMaker(new Date().getTime()));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            final var c = new Client(Integer.toString(++i));
            try {
                barberShop.newClientArrived(c);
            } catch (BarberShopOutOfServiceException | BarberShopReachedMaxCapacity e) {
                System.out.println(e);
            }
        }
        barberShop.printStatisctics();
    }

    public static int clientRandomArriveTimeMaker(long ms) {
        var time = ms % BarberShop.DAY_LENGTH;
        Double x = time / (BarberShop.DAY_LENGTH / Math.PI);
        var formula = (int)((2 - ((Math.abs(Math.sin(x)) * Math.abs(Math.cos(8 * x)) + 1) * Math.abs(Math.sin(x)))) * ((new Random()).nextInt(100) + 200));
        formula += 5 + new Random().nextInt(10);
        var closingHours = time < BarberShop.SERVICE_START_TIME || time > BarberShop.SERVICE_END_TIME;
        if (closingHours) {
            if ((formula * 10) % BarberShop.DAY_LENGTH > BarberShop.SERVICE_START_TIME) {
                if (time < BarberShop.SERVICE_START_TIME) return BarberShop.SERVICE_START_TIME - (int)time;
                return BarberShop.DAY_LENGTH - (int)time + BarberShop.SERVICE_START_TIME;
            }
            return formula * 10;
        }
        return formula;
    }
}
