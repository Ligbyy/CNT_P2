import java.util.Random;

public class Withdrawal implements Runnable {
    private final BankAcc account;
    private final boolean isMultiCore;
    private String name;

    public Withdrawal(BankAcc account, boolean isMultiCore,String name) {
        this.account = account;
        this.isMultiCore = isMultiCore;
        this.name = name;
    }

    @Override
    public void run() {
        Random rand = new Random();
        while (true) {
            int amount = 1 + rand.nextInt(99); // Withdrawal amount between $1 and $99
            if (account.withdraw(amount,getName())) {
                try {
                    if (isMultiCore) {
                        Thread.sleep(rand.nextInt(100)); // Sleep for multi-core processors
                    } else {
                        Thread.yield(); // Yield for single-core processors
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
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
