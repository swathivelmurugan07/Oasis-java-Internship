import java.util.ArrayList;
import java.util.Scanner;
// Class 1: Transaction Record
class Transaction {
    String type;
    double amount;
    public Transaction(String type, double amount) {
        this.type = type;
        this.amount = amount;
    }
}
// Class 2: User Account
class User {
    private String userId = "user123";
    private String userPin = "1234";
    private double balance = 1000.0;
    private ArrayList<Transaction> history = new ArrayList<>();
    public boolean authenticate(String id, String pin) {
        return userId.equals(id) && userPin.equals(pin);
    }
    public double getBalance() { return balance; }
    public void deposit(double amount) {
        balance += amount;
        history.add(new Transaction("Deposit", amount));
    }
    public boolean withdraw(double amount) {
        if (amount <= balance) {
            balance -= amount;
            history.add(new Transaction("Withdrawal", amount));
            return true;
        }
        return false;
    }
    public void showHistory() {
        System.out.println("\n--- Transaction History ---");
        for (Transaction t : history) {
            System.out.println(t.type + ": $" + t.amount);
        }
    }
}
// Class 3: ATM Operations Logic
// Class 4: Interface Management
// Class 5: Main Entry Point (ATMSystem)
public class ATMSystem {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        User currentUser = new User();
        System.out.println("Welcome to the ATM");
        System.out.print("Enter User ID: ");
        String id = sc.nextLine();
        System.out.print("Enter PIN: ");
        String pin = sc.nextLine();
        if (currentUser.authenticate(id, pin)) {
            boolean quit = false;
            while (!quit) {
                System.out.println("\n1. History 2. Withdraw 3. Deposit 4. Transfer 5. Quit");
                System.out.print("Choose option: ");
                int choice = sc.nextInt();
                switch (choice) {
                    case 1 -> currentUser.showHistory();
                    case 2 -> {
                        System.out.print("Amount to withdraw: ");
                        double amt = sc.nextDouble();
                        if(currentUser.withdraw(amt)) System.out.println("Success!");
                        else System.out.println("Insufficient funds.");
                    }
                    case 3 -> {
                        System.out.print("Amount to deposit: ");
                        double amt = sc.nextDouble();
                        currentUser.deposit(amt);
                        System.out.println("Deposited.");
                    }
                    case 4 -> System.out.println("Transfer feature coming soon!");
                    case 5 -> quit = true;
                }
            }
        } else {
            System.out.println("Invalid Credentials!");
        }
        sc.close();
    }
                          }
