import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) {
        System.out.println("* * * SIMULATION BEGINS...\n");
        System.out.println("Deposit Agents                   Withdrawal Agents                   Balance                                              Transaction Number");
        System.out.println("--------------                   -----------------                   -------                                              ------------------");

        boolean isMultiCore = Runtime.getRuntime().availableProcessors() > 1;
        BankAcc account = new BankAcc();
        
        ExecutorService executor = Executors.newFixedThreadPool(17);

        Withdrawal WT0 = new Withdrawal(account, isMultiCore,"WT0");
        Withdrawal WT1 = new Withdrawal(account, isMultiCore,"WT1");
        Withdrawal WT2 = new Withdrawal(account, isMultiCore,"WT2");
        Withdrawal WT3 = new Withdrawal(account, isMultiCore,"WT3");
        Withdrawal WT4 = new Withdrawal(account, isMultiCore,"WT4");
        Withdrawal WT5 = new Withdrawal(account, isMultiCore,"WT5");
        Withdrawal WT6 = new Withdrawal(account, isMultiCore,"WT6");
        Withdrawal WT7 = new Withdrawal(account, isMultiCore,"WT7");
        Withdrawal WT8 = new Withdrawal(account, isMultiCore,"WT8");
        Withdrawal WT9 = new Withdrawal(account, isMultiCore,"WT9");
        executor.execute(WT0);
        executor.execute(WT1);
        executor.execute(WT2);
        executor.execute(WT3);
        executor.execute(WT4);
        executor.execute(WT5);
        executor.execute(WT6);
        executor.execute(WT7);
        executor.execute(WT8);
        executor.execute(WT9);
        
        executor.execute(new Auditor(account, "InternalBank",0));
        

        Depositor DT0 = new Depositor(account,"DT0");
        Depositor DT1 = new Depositor(account,"DT1");
        Depositor DT2 = new Depositor(account,"DT2");
        Depositor DT3 = new Depositor(account,"DT3");
        Depositor DT4 = new Depositor(account,"DT4");
        executor.execute(DT0);
        executor.execute(DT1);
        executor.execute(DT2);
        executor.execute(DT3);
        executor.execute(DT4);

        
        executor.execute(new Auditor(account, "TreasuryDepartment",0));
     
    }
}