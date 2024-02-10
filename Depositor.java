import java.util.Random;

public class Depositor implements Runnable {
    private final BankAcc account;
    private String name;

    public Depositor(BankAcc account, String name) {
        this.account = account;
        this.name = name;
    }

    @Override
    public void run() {
        Random rand = new Random();
        while (true) {
            int amount = 1 + rand.nextInt(500); // Deposit amount between $1 and $500
            account.deposit(amount,getName());
            try {
                Thread.sleep(rand.nextInt(200)); // Sleep for a random time
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    
}