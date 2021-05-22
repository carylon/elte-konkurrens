package barbershop;

import java.util.ArrayList;
import java.util.Stack;

import javax.naming.LimitExceededException;

import client.Client;
import barbershop.barber.Barber;

public class BarberShop {
    private static final int MAX_CLIENT = 5;

    final private Stack<Client> waitList;
    final private ArrayList<Barber> barbers;

    public BarberShop() {
        this.waitList = new Stack<>();
        this.barbers = new ArrayList<>();

        this.initBarbers();
    }

    private void initBarbers() {
        this.barbers.add(new Barber());
    }

    public void addClient(Client c) throws LimitExceededException {
        if (this.waitList.size() >= MAX_CLIENT) {
            throw new LimitExceededException("BarberShop.addClient: Max client limit reached (" + MAX_CLIENT + ").");
        }
        this.waitList.add(c);
    }
}
