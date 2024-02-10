import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BankAcc {

    private double balance = 0;
    private final Lock lock = new ReentrantLock();
    private final Condition sufficientFunds = lock.newCondition();
    private final AtomicInteger transactionNumber = new AtomicInteger(1);
    private final Condition auditorActive = lock.newCondition();
    private final AtomicBoolean isAuditorActive = new AtomicBoolean(false);

    // Method to deposit money
    public void deposit(double amount, String name) {
        lock.lock();
        try {
            while (isAuditorActive.get()) {
                auditorActive.await();
            }
            
            balance += amount;
            // Formatting the message to align with the example provided
            String actionMsg = String.format("%-39s", "Agent " + name + " deposits $" + amount) + String.format("%-31s", " ") ;
            String balanceMsg = String.format("%-36s", "(+) Balance is $" + getBalance());
            String transactionMsg = String.format("%-6s", "                    " + transactionNumber.getAndIncrement());            
            System.out.println(actionMsg + balanceMsg + transactionMsg);
    
            if (amount > 350) {
                System.out.println("* * * Flagged Transaction - Depositor Agent " + name + " Made A Deposit In Excess Of $350.00 USD - See Flagged Transaction Log.\n");
            }
            
            sufficientFunds.signalAll();
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            lock.unlock();
        }
    }
    
    

    // Method to withdraw money
    public boolean withdraw(double amount, String name) {
        lock.lock();
        try {
            while (isAuditorActive.get()) {
                auditorActive.await();
            }
    
            while (balance < amount) {
                String blockedMsg = String.format("%-35s", " ") + String.format("%-35s", "Agent " + name + " Withdraws $" + amount + " (******) WITHDRAWAL BLOCKED - INSUFFICIENT FUNDS!!!");
                System.out.println(blockedMsg);
                sufficientFunds.await();
            }
    
            balance -= amount;
            String withdrawalMsg = String.format("%-35s", " ") + String.format("%-35s", "Agent " + name + " Withdraws $" + amount);
            String balanceMsg = String.format("%-50s", "(-) Balance is $" + getBalance());
            String transactionMsg = String.format("      %-10d", transactionNumber.getAndIncrement());
            System.out.println(withdrawalMsg + balanceMsg + transactionMsg);
    
            if (amount > 75) {
                System.out.println();
                System.out.println("* * * Flagged Transaction - Withdrawal Agent " + name + " Made A Withdrawal In Excess Of $75.00 USD - See Flagged Transaction Log.\n");
            }
            
            return true;
        } catch (InterruptedException e) {
            return false;
        } finally {
            lock.unlock();
        }
    }

    public void startAudit() {
        lock.lock();
        try {
            isAuditorActive.set(true);
        } finally {
            lock.unlock();
        }
    }

    public void endAudit() {
        lock.lock();
        try {
            isAuditorActive.set(false);
            auditorActive.signalAll(); // Signal all waiting threads that audit is complete
        } finally {
            lock.unlock();
        }
    }

    // Getters for balance and transaction number for the auditors
    public double getBalance() {
        lock.lock();
        try {
            return balance;
        } finally {
            lock.unlock();
        }
    }

    public int getTransactionNumber() {
        return transactionNumber.get();
    }
    
}
