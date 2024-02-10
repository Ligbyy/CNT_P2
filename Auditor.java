import java.util.Random;

public class Auditor implements Runnable {
    private final BankAcc account;
    private final String auditorType; // For distinguishing between different auditors, e.g., "InternalBank", "TreasuryDept"
    private int lastTransactIndex;

    public Auditor(BankAcc account, String auditorType, int lastTransactIndex) {
        this.account = account;
        this.auditorType = auditorType;
        this.lastTransactIndex = lastTransactIndex;
    }

    @Override
    public void run() {

        Random randInitial = new Random();
        try {
            // Initial delay before the first audit
            Thread.sleep(randInitial.nextInt(300));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Handle interrupted exception
            return; // Exit if the thread was interrupted during sleep
        }

        Random rand = new Random();
        while (true) {
            // Simulate auditing process
            account.startAudit();
            double balance = account.getBalance();
            int transactionCount = account.getTransactionNumber();
            lastTransactIndex = getLastTransactIndex();
            System.out.println("\n\n****************************************************************************************************************************************************");
            System.out.println();
                if (getAuditorType().equalsIgnoreCase("InternalBank")) {
                    System.out.println("           INTERNAL BANK AUDITOR FINDS CURRENT ACCOUNT BALANCE TO BE $" + balance + "        Number of Transactions Since Last Internal Audit is: " + (transactionCount - lastTransactIndex));
                }else{
                    System.out.println("           TREASURY DEPT AUDITOR FINDS CURRENT ACCOUNT BALANCE TO BE $" + balance + "        Number of Transactions Since Last Treasury Audit is: " + (transactionCount - lastTransactIndex));
                }
            System.out.println();
            System.out.println("****************************************************************************************************************************************************\n\n");
            setLastTransactIndex(transactionCount);
            account.endAudit();
            try {
                Thread.sleep(200 + rand.nextInt(500)); // Auditors run less frequently, hence longer sleep
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public String getAuditorType() {
        return auditorType;
    }

    public int getLastTransactIndex() {
        return lastTransactIndex;
    }

    public void setLastTransactIndex(int lastTransactIndex) {
        this.lastTransactIndex = lastTransactIndex;
    }

    

    
}
