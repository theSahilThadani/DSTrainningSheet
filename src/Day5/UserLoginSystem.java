package Day5;

import java.util.HashMap;
import java.util.Scanner;

public class UserLoginSystem {
    private static HashMap<String, String> users;

    public static void registerUser(String username, String password) {
        if (users.containsKey(username)) {
            System.out.println("User already exists!");
            return;
        }
        users.put(username, password);
        System.out.println(" User registered: " + username);

    }

    public static boolean loginUser(String username, String password) {
        if (!users.containsKey(username)) {
            System.out.println("User not found!");
            return false;
        }

        String storedPassword = users.get(username);
        if (storedPassword.equals(password)) {
            System.out.println("Login successful " + username);
            return true;
        } else {
            System.out.println("Incorrect password!");
            return false;
        }
    }
    public static void DisplayOptions() {
        System.out.println("=".repeat(50) + "\n");
        System.out.println("Welcome to Login System");
        System.out.println("=".repeat(50) + "\n");
        System.out.println("1. User Login \n");
        System.out.println("2. User Register \n");
        System.out.println("3. User Logout \n");
        System.out.println("=".repeat(50) + "\n");
    }
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        users = new HashMap<>();
        boolean session = true;
        while(session) {
            DisplayOptions();
            int choice =  sc.nextInt();
            sc.nextLine();
            switch (choice) {
                case 1:
                    System.out.println("Enter username: ");
                    String username = sc.nextLine();
                    System.out.println("Enter password: ");
                    String password = sc.nextLine();
                    loginUser(username, password);
                    break;
                case 2:
                    System.out.println("Enter username: ");
                    String Regusername = sc.nextLine();
                    System.out.println("Enter password: ");
                    String Regpassword = sc.nextLine();
                    registerUser(Regusername, Regpassword);
                    break;
                case 3:
                    session = false;
            }
        }
    }

}
