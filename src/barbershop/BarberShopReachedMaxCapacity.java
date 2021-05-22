package barbershop;

public class BarberShopReachedMaxCapacity extends Exception {
    public BarberShopReachedMaxCapacity(short capacity) {
        super("Barber shop reached the max capacity (" + capacity + ").");
    }
}
