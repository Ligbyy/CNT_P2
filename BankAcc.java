import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BankAcc {

    private double balance = 0;
    private final Lock lock = new ReentrantLock();
    private final Condition sufficientFunds = lock.newCondition();
    private final AtomicInteger transactionNumber = new AtomicInteger(1);

    // Method to deposit money
    public void deposit(double amount, String name) {
        lock.lock();
        try {

            if (amount > 350) {
                System.out.println("* * * Flagged Transaction - Depositor Agent " + name  + " Made A Deposit In Excess Of $350.00 USD - See Flagged Transaction Log.");
                // Log to file if required
            }
                balance += amount;
            System.out.println("Deposit: " + amount + ", New Balance: " + balance + ", Transaction Number: " + transactionNumber.getAndIncrement());
            sufficientFunds.signalAll(); // Signal withdrawal threads that balance has been updated
            
            
        } finally {
            lock.unlock();
        }
    }

    // Method to withdraw money
    public boolean withdraw(double amount, String name) {
        lock.lock();
        try {
            while (balance < amount) {
                System.out.println("Agent " + name + " Withdraws $" + amount + " (******) WITHDRAWAL BLOCKED - INSUFFICIENT FUNDS!!!");
                sufficientFunds.await(); // Wait until sufficient funds are available
            }
                if (amount > 75) {
                System.out.println("* * * Flagged Transaction - Withdrawal Agent " + name  + " Made A Withdrawal In Excess Of $75.00 USD - See Flagged Transaction Log.");
                // Log to file if required
                }
            balance -= amount;
            System.out.println("Withdrawal: " + amount + ", New Balance: " + balance + ", Transaction Number: " + transactionNumber.getAndIncrement());
            return true;
        } catch (InterruptedException e) {
            return false;
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
