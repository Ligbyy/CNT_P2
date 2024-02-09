import java.util.Random;

public class Auditor implements Runnable {
    private final BankAcc account;
    private final String auditorType; // For distinguishing between different auditors, e.g., "InternalBank", "TreasuryDept"

    public Auditor(BankAcc account, String auditorType) {
        this.account = account;
        this.auditorType = auditorType;
    }

    @Override
    public void run() {
        Random rand = new Random();
        while (true) {
            // Simulate auditing process
            double balance = account.getBalance();
            int transactionCount = account.getTransactionNumber();
            System.out.println(auditorType + " Auditor: Current Balance: " + balance + ", Transactions Count: " + transactionCount);
            
            try {
                Thread.sleep(500 + rand.nextInt(500)); // Auditors run less frequently, hence longer sleep
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
