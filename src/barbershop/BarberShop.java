package barbershop;

import java.util.ArrayList;
import java.util.Date;
import java.util.Stack;

import javax.naming.LimitExceededException;

import client.Client;
import barbershop.barber.Barber;

public class BarberShop {
    private static final short MAX_CLIENT = 5;
    public static final short DAY_LENGTH = 9600;
    public static final short SERVICE_START_TIME = 3200;
    public static final short SERVICE_END_TIME = 6400;

    private boolean finallyClosed;
    private final Date date;
    private final Stack<Client> waitList;
    private final ArrayList<Barber> barbers;


    public BarberShop() {
        this.finallyClosed = false;
        this.date = new Date();
        this.waitList = new Stack<>();
        this.barbers = new ArrayList<>();

        this.initBarbers();
    }

    private void initBarbers() {
        this.barbers.add(Barber.createBarber(this));
    }

    public void addClient(Client c) throws LimitExceededException, BarberShopOutOfServiceException {
        if (this.waitList.size() >= MAX_CLIENT) {
            throw new LimitExceededException("BarberShop.addClient: Max client limit reached (" + MAX_CLIENT + ").");
        }

        /** 
         * IMPORTANT!:
         * We do not reset the timer, when the barber shop start serving.
         * In real life we do use the time that all the world use,
         * so we always check the OS time to determine what is the actual time,
         * instead of calculating it from a specific date.
        */
        long time = this.date.getTime() % DAY_LENGTH;
        if (time < SERVICE_START_TIME || time > SERVICE_END_TIME) {
            throw new BarberShopOutOfServiceException();
        }

        this.waitList.add(c);
    }

    public boolean getFinallyClosed() {
        return this.finallyClosed;
    }
}
