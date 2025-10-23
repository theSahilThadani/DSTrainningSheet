import Day6_7.entities.User;
import Day6_7.entities.UserInfo;
import Day6_7.entities.UserStatus;
import Day6_7.managers.UserManager;

import java.util.List;
import java.util.Scanner;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    private static final UserManager userManager = new UserManager();
    private static User currentUser = null;
    public static void main(String[] args) {
        System.out.println("\n" + "═".repeat(80));
        System.out.println("🚀 CHAT APP - USER SERVICE TEST");
        System.out.println("═".repeat(80) + "\n");

        // Pre-populate with test users
        createTestUsers();

        // Interactive menu
        try (Scanner sc = new Scanner(System.in)) {
            boolean running = true;

            while (running) {
                if (currentUser == null) {
                    displayGuestMenu();
                    running = handleGuestMenu(sc);
                } else {
                    displayUserMenu();
                    running = handleUserMenu(sc);
                }
            }
        }

        System.out.println("\n✅ Chat App closed. Goodbye! 👋\n");
    }

    private static void createTestUsers() {
        System.out.println("📌 Creating test users...\n");

        userManager.registerUser("alice", "password123", "Alice Smith");
        userManager.registerUser("ali", "password123", "Ali Smith");
        userManager.registerUser("alce", "password123", "Ace Smith");
        userManager.registerUser("ace", "password123", "Ale ith");
        userManager.registerUser("bob", "password123", "Bob Johnson");
        userManager.registerUser("charlie", "password123", "Charlie Brown");
        userManager.registerUser("diana", "password123", "Diana Prince");
        userManager.registerUser("eve", "password123", "Eve Anderson");

        System.out.println();
    }

    private static void displayGuestMenu() {
        System.out.println("\n┌─────────────────────────────────┐");
        System.out.println("│   🌐 CHAT APP (Not Logged In)  │");
        System.out.println("├─────────────────────────────────┤");
        System.out.println("│ 1. Register                     │");
        System.out.println("│ 2. Login                        │");
        System.out.println("│ 3. View online users            │");
        System.out.println("│ 4. Statistics                   │");
        System.out.println("│ 5. Exit                         │");
        System.out.println("└─────────────────────────────────┘");
        System.out.print("Enter choice: ");
    }

    private static void displayUserMenu() {
        System.out.println("\n┌─────────────────────────────────┐");
        System.out.printf("│   👤 %s%-22s│\n", currentUser.getDisplayName(), "");
        System.out.println("├─────────────────────────────────┤");
        System.out.println("│ 1. Change status                │");
        System.out.println("│ 2. View online users            │");
        System.out.println("│ 3. Search users                 │");
        System.out.println("│ 4. Update display name          │");
        System.out.println("│ 5. Statistics                   │");
        System.out.println("│ 6. Logout                       │");
        System.out.println("└─────────────────────────────────┘");
        System.out.print("Enter choice: ");
    }

    private static boolean handleGuestMenu(Scanner sc) {
        int choice = getIntInput(sc);
        System.out.println();

        switch (choice) {
            case 1:
                handleRegistration(sc);
                break;
            case 2:
                handleLogin(sc);
                break;
            case 3:
                viewOnlineUsers();
                break;
            case 5:
                return false;
            default:
                System.out.println("❌ Invalid choice");
        }

        return true;
    }

    private static boolean handleUserMenu(Scanner sc) {
        int choice = getIntInput(sc);
        System.out.println();

        switch (choice) {
            case 1:
                changeStatus(sc);
                break;
            case 2:
                viewOnlineUsers();
                break;
            case 3:
                searchUsers(sc);
                break;
            case 4:
                updateDisplayName(sc);
                break;
            case 6:
                handleLogout();
                break;
            default:
                System.out.println("❌ Invalid choice");
        }

        return true;
    }

    private static void handleRegistration(Scanner sc) {
        System.out.print("Enter username: ");
        String username = sc.nextLine().trim();

        System.out.print("Enter password: ");
        String password = sc.nextLine();

        System.out.print("Enter display name: ");
        String displayName = sc.nextLine().trim();

        try {
            User user = userManager.registerUser(username, password, displayName);
            if (user != null) {
                currentUser = user;
                userManager.updateUserStatus(user, UserStatus.ONLINE);
            }
        } catch (IllegalArgumentException e) {
            System.out.println("❌ " + e.getMessage());
        }
    }

    private static void handleLogin(Scanner sc) {
        System.out.print("Enter username: ");
        String username = sc.nextLine().trim();

        System.out.print("Enter password: ");
        String password = sc.nextLine();

        try {
            User user = userManager.loginUser(username, password);
            if (user != null) {
                currentUser = user;
            }
        } catch (IllegalArgumentException e) {
            System.out.println("❌ " + e.getMessage());
        }
    }

    private static void handleLogout() {
        if (currentUser != null) {
            userManager.logoutUser(currentUser);
            currentUser = null;
        }
    }

    private static void changeStatus(Scanner sc) {
        System.out.println("Select status:");
        UserStatus[] statuses = UserStatus.values();
        for (int i = 0; i < statuses.length; i++) {
            System.out.printf("%d. %s\n", i + 1, statuses[i]);
        }

        System.out.print("Enter choice: ");
        int choice = getIntInput(sc);

        if (choice >= 1 && choice <= statuses.length) {
            userManager.updateUserStatus(currentUser, statuses[choice - 1]);
        } else {
            System.out.println("❌ Invalid choice");
        }
    }

    private static void viewOnlineUsers() {
        List<UserInfo> onlineUsers = userManager.getUsersByStatus(UserStatus.ONLINE);

        System.out.println("\n" + "═".repeat(80));
        System.out.println("🟢 ONLINE USERS (" + onlineUsers.size() + ")");
        System.out.println("═".repeat(80));

        if (onlineUsers.isEmpty()) {
            System.out.println("No users online");
        } else {
            for (UserInfo user : onlineUsers) {
                System.out.println(user);
            }
        }

        System.out.println("═".repeat(80) + "\n");
    }

    private static void searchUsers(Scanner sc) {
        System.out.print("Enter username prefix: ");
        String prefix = sc.nextLine().trim();

        List<UserInfo> results = userManager.searchUsersByPrefix(prefix);

        System.out.println("\n🔍 Search results for '" + prefix + "': " + results.size());
        for (UserInfo user : results) {
            System.out.println("  " + user);
        }
        System.out.println();
    }

    private static void updateDisplayName(Scanner sc) {
        System.out.print("Enter new display name: ");
        String newName = sc.nextLine().trim();

        try {
            currentUser.setDisplayName(newName);
            System.out.println("✅ Display name updated");
        } catch (IllegalArgumentException e) {
            System.out.println("❌ " + e.getMessage());
        }
    }

    private static int getIntInput(Scanner sc) {
        while (true) {
            try {
                String input = sc.nextLine().trim();
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.print("❌ Invalid number. Try again: ");
            }
        }
    }
}