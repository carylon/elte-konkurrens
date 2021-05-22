import java.util.ArrayList;

import barbershop.BarberShop;
import barbershop.BarberShopOutOfServiceException;
import barbershop.BarberShopReachedMaxCapacity;
import client.Client;

class Main {
    public static void main(String[] args) {
        final BarberShop bs = new BarberShop();
        ArrayList<Client> clients = new ArrayList<>();
        clients.add(new Client("András"));
        clients.add(new Client("Huba"));
        clients.add(new Client("Elemér"));
        clients.add(new Client("Zsófi"));
        clients.add(new Client("Karcsi"));
        clients.add(new Client("Katalin"));
        
        int i = 0;
        while (i < clients.size() && bs.getFinallyClosed() == false) {
            System.out.println("While");
            try {
                bs.addClient(clients.get(i));
                i++;
            } catch (BarberShopReachedMaxCapacity e) {
                // Do nothing for now
                System.out.println(e);
            } catch (BarberShopOutOfServiceException e) {
                // Do nothing for now
                System.out.println(e);
            }
            try {
                Thread.sleep(300);
            } catch (Exception e) {
                System.out.println("Sleep failed");
            }
        }
        System.out.println("Main process finished");
    }
}