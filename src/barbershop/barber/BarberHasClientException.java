package barbershop.barber;

public class BarberHasClientException extends Exception {
    public BarberHasClientException() {
        super("This barber is already has a client.");
    }
}
