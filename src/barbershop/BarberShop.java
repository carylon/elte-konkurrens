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
    private final LinkedList<Client> waitList;
    private final LinkedList<Barber> barbers;


    public BarberShop() {
        this.finallyClosed = false;
        this.waitList = new LinkedList<>();
        this.barbers = new LinkedList<>();

        this.initBarbers();

        Timer t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                BarberShop.this.finallyClosed = true;
            }
        }, (long) (DAY_LENGTH * 5));
    }

    private void initBarbers() {
        this.barbers.add(Barber.createBarber(this));
    }

    public void addClient(Client c) throws BarberShopReachedMaxCapacity, BarberShopOutOfServiceException {
        if (this.waitList.size() >= MAX_CLIENT) {
            throw new BarberShopReachedMaxCapacity(MAX_CLIENT);
        }

        /** 
         * IMPORTANT!:
         * We do not reset the timer, when the barber shop start serving.
         * In real life we do use the time that all the world use,
         * so we always check the OS time to determine what is the actual time,
         * instead of calculating it from a specific date.
        */
        Date date = new Date();
        long time = date.getTime() % DAY_LENGTH;
        if (time < SERVICE_START_TIME || time > SERVICE_END_TIME) {
            throw new BarberShopOutOfServiceException();
        }

        System.out.println("Client added: " + c.getName());
        this.waitList.addLast(c);
        this.barbers.forEach((b) -> {
            synchronized(b) {
                b.notify();
            }
        });
    }

    public boolean getFinallyClosed() {
        return this.finallyClosed;
    }

    public void barberFinishedWithAClient(Barber barber, Client client, int time) {
        System.out.println("Barber finished with " + client.getName() + ", in " + time + "ms.");
    }

    public Client getNextClient() {
        return this.waitList.poll();
    }
}
