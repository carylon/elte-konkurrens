package barbershop;

public class BarberShopOutOfServiceException extends Exception {
    public BarberShopOutOfServiceException() {
        super("Barber shop is closed right now. Come back later.");
    }
}
