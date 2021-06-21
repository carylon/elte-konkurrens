package client;

public class Client {
    private final String name;

    private final ClientWants needs;

    public Client(String name, ClientWants needs) {
        this.name = name;
        this.needs = needs;
    }

    public String getName() {
        return this.name;
    }

    public ClientWants getNeeds() {
        return this.needs;
    }
}
