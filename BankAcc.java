import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
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

    public void deposit(double amount, String name) {
        lock.lock();
        try {
            while (isAuditorActive.get()) {
                auditorActive.await();
            }
            
            balance += amount;
            String actionMsg = String.format("%-39s", "Agent " + name + " deposits $" + amount) + String.format("%-31s", " ") ;
            String balanceMsg = String.format("%-36s", "(+) Balance is $" + getBalance());
            String transactionMsg = String.format("%-6s", "                    " + transactionNumber.getAndIncrement());            
            System.out.println(actionMsg + balanceMsg + transactionMsg);
    
            if (amount > 350) {
                
                System.out.println("* * * Flagged Transaction - Depositor Agent " + name + " Made A Deposit In Excess Of $350.00 USD - See Flagged Transaction Log.\n");
                logFlaggedTransaction("Depositor Agent " + name, amount, "issued deposit of", transactionNumber.getAndIncrement());
            }
            
            sufficientFunds.signalAll();
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            lock.unlock();
        }
    }
    
    

  
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
                System.out.println("\n* * * Flagged Transaction - Withdrawal Agent " + name + " Made A Withdrawal In Excess Of $75.00 USD - See Flagged Transaction Log.\n");
                logFlaggedTransaction("      Withdrawal Agent " + name, amount, "issued withdrawal of", transactionNumber.getAndIncrement());
            }
            
            return true;
        } catch (InterruptedException e) {
            return false;
        } finally {
            lock.unlock();
        }
    }

    private void logFlaggedTransaction(String agentName, double amount, String transactionType, int transactionNumber) {
    String filename = "C:\\Users\\blueg\\VSCodeprojects\\CNT_P2\\transaction.csv";
    try (FileWriter fw = new FileWriter(filename, true); PrintWriter pw = new PrintWriter(fw)) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss z");
        String timestamp = ZonedDateTime.now().format(dtf);
        String logEntry = String.format("%s, %s $%.2f at: %s, Transaction Number : %d%n", 
                                        agentName, transactionType, amount, timestamp, transactionNumber);
        pw.print(logEntry);
    } catch (IOException e) {
        e.printStackTrace();
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
            auditorActive.signalAll(); 
        } finally {
            lock.unlock();
        }
    }

   
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
