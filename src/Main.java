import barbershop.BarberShop;
import barbershop.BarberShopOutOfServiceException;
import barbershop.BarberShopReachedMaxCapacity;
import client.Client;

class Main {
    public static void main(String[] args) {
        BarberShop bs = new BarberShop();
        Client c1 = new Client();
        Client c2 = new Client();
        Client c3 = new Client();
        Client c4 = new Client();
        Client c5 = new Client();
        Client c6 = new Client();

        try {
            bs.addClient(c1);
            bs.addClient(c2);
            bs.addClient(c3);
            bs.addClient(c4);
            bs.addClient(c5);
            bs.addClient(c6);
        } catch (BarberShopReachedMaxCapacity e) {
            System.out.println(e);
        } catch (BarberShopOutOfServiceException e) {
            System.out.println(e);
        }
    }
}