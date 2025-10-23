package Day6_7.managers;

import Day6_7.entities.User;
import Day6_7.entities.UserInfo;
import Day6_7.entities.UserStatus;
import Day6_7.entities.Conversation;
import Day6_7.entities.Message;

import java.util.*;


/**
 * ChatApp - CLI integration of UserManager and ConversationManager
 *
 * Assumes the following classes exist in your project:
 * - managers.UserManager
 * - managers.ConversationManager
 * - entities.User, Message, Conversation, ...
 *
 * Run:
 *   javac -d out $(find . -name "*.java")
 *   java -cp out app.ChatApp
 */
public class ChatApp {

    private final UserManager userManager;
    private final ConversationManager conversationManager;

    private User currentUser;

    public ChatApp() {
        this.userManager = new UserManager();
        this.conversationManager = new ConversationManager();

        // optional: pre-populate some users for demo
        userManager.registerUser("alice", "password123", "Alice");
        userManager.registerUser("bob", "password123", "Bob");
        userManager.registerUser("charlie", "password123", "Charlie");
    }

    public static void main(String[] args) {
        ChatApp app = new ChatApp();
        try (Scanner sc = new Scanner(System.in)) {
            app.run(sc);
        }
    }

    private void run(Scanner sc) {
        boolean running = true;

        printHeader();

        while (running) {
            try {
                if (currentUser == null) {
                    showGuestMenu();
                    int choice = getIntInput(sc, "Choose: ");
                    switch (choice) {
                        case 1 -> handleRegister(sc);
                        case 2 -> handleLogin(sc);
                        case 3 -> listOnlineUsers();
                        case 4 -> searchUsers(sc);
                        case 5 -> running = false;
                        default -> System.out.println("Invalid choice");
                    }
                } else {
                    showUserMenu();
                    int choice = getIntInput(sc, "Choose: ");
                    switch (choice) {
                        case 1 -> changeStatus(sc);
                        case 2 -> listOnlineUsers();
                        case 3 -> searchUsers(sc);
                        case 4 -> startOrOpenChat(sc);
                        case 5 -> viewMyConversations();
                        case 6 -> viewConversationMessages(sc);
                        case 7 -> sendMessageToUser(sc);
                        case 8 -> undoLastMessage(sc);
                        case 9 -> markConversationRead(sc);
                        case 10 -> { userManager.logoutUser(currentUser); currentUser = null; }
                        case 11 -> { // Exit
                            System.out.println("Goodbye!");
                            running = false;
                        }
                        default -> System.out.println("Invalid choice");
                    }
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void printHeader() {
        System.out.println("\n" + "═".repeat(80));
        System.out.println("🚀 CHAT APP - Integrated Demo");
        System.out.println("═".repeat(80));
    }

    private void showGuestMenu() {
        System.out.println("\n--- Not logged in ---");
        System.out.println("1. Register");
        System.out.println("2. Login");
        System.out.println("3. View online users");
        System.out.println("4. Search users");
        System.out.println("5. Exit");
    }

    private void showUserMenu() {
        System.out.println("\n--- Logged in as: " + currentUser.getDisplayName() + " (@" + currentUser.getUsername() + ") ---");
        System.out.println("1. Change status");
        System.out.println("2. View online users");
        System.out.println("3. Search users");
        System.out.println("4. Start/open chat with user");
        System.out.println("5. View my conversations");
        System.out.println("6. View messages in a conversation");
        System.out.println("7. Send message to user");
        System.out.println("8. Undo last message");
        System.out.println("9. Mark conversation as read");
        System.out.println("10. Logout");
        System.out.println("11. Exit");
    }

    private void handleRegister(Scanner sc) {
        System.out.print("Enter username: ");
        String username = sc.nextLine().trim();
        System.out.print("Enter password: ");
        String password = sc.nextLine();
        System.out.print("Enter display name (optional): ");
        String displayName = sc.nextLine().trim();
        try {
            User user = userManager.registerUser(username, password, displayName.isEmpty() ? username : displayName);
            if (user != null) {
                currentUser = user;
                userManager.updateUserStatus(currentUser, UserStatus.ONLINE);
                System.out.println("Registered and logged in as " + currentUser.getUsername());
            } else {
                System.out.println("Registration failed (username taken?)");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Registration error: " + e.getMessage());
        }
    }

    private void handleLogin(Scanner sc) {
        System.out.print("Enter username: ");
        String username = sc.nextLine().trim();
        System.out.print("Enter password: ");
        String password = sc.nextLine();

        try {
            User user = userManager.loginUser(username, password);
            if (user != null) {
                currentUser = user;
                System.out.println("Logged in as " + currentUser.getUsername());
            } else {
                System.out.println("Login failed (invalid credentials)");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Login error: " + e.getMessage());
        }
    }

    private void changeStatus(Scanner sc) {
        System.out.println("Choose status:");
        UserStatus[] statuses = UserStatus.values();
        for (int i = 0; i < statuses.length; i++) {
            System.out.printf("%d. %s%n", i + 1, statuses[i]);
        }
        int choice = getIntInput(sc, "Status: ");
        if (choice >= 1 && choice <= statuses.length) {
            userManager.updateUserStatus(currentUser, statuses[choice - 1]);
        } else {
            System.out.println("Invalid status");
        }
    }

    private void listOnlineUsers() {
        List<UserInfo> online = userManager.getUsersByStatus(UserStatus.ONLINE);
        System.out.println("\n🟢 ONLINE USERS (" + online.size() + ")");
        for (UserInfo ui : online) {
            System.out.println(" - " + ui);
        }
    }

    private void searchUsers(Scanner sc) {
        System.out.print("Enter username prefix (or full): ");
        String q = sc.nextLine().trim();
        if (q.isEmpty()) {
            System.out.println("Empty query");
            return;
        }
        List<UserInfo> results = userManager.searchUsersByPrefix(q);
        System.out.println("Results (" + results.size() + "):");
        for (UserInfo ui : results) {
            System.out.println(" - " + ui);
        }
    }

    private void startOrOpenChat(Scanner sc) {
        System.out.print("Enter username to chat with: ");
        String username = sc.nextLine().trim();
        if (username.equalsIgnoreCase(currentUser.getUsername())) {
            System.out.println("Cannot chat with yourself");
            return;
        }
        User other = userManager.getUserByUsername(username);
        if (other == null) {
            System.out.println("User not found: " + username);
            return;
        }
        Conversation conv = conversationManager.getOrCreateConversation(currentUser.getUserId(), other.getUserId());
        System.out.println("Opened conversation: " + conv.getConversationId() + " with " + other.getUsername());
        List<Message> recent = conv.getRecentMessages(20);
        if (recent.isEmpty()) {
            System.out.println("No messages yet.");
        } else {
            System.out.println("Recent messages:");
            for (Message m : recent) {
                System.out.printf("[%s] %s: %s%n", m.getFormattedTimestamp(), m.getSenderId(), m.getContent());
            }
        }
    }

    private void viewMyConversations() {
        List<Conversation> convs = conversationManager.getUserConversations(currentUser.getUserId());
        if (convs.isEmpty()) {
            System.out.println("No conversations");
            return;
        }
        System.out.println("\nYour conversations:");
        for (Conversation c : convs) {
            String other = c.getOtherParticipant(currentUser.getUserId());
            int unread = c.getUnreadCount(currentUser.getUserId());
            System.out.printf(" - %s (convId=%s) unread=%d lastActivity=%s%n",
                    other, c.getConversationId(), unread, c.getTimeSinceLastActivity() + "ms");
        }
    }

    private void viewConversationMessages(Scanner sc) {
        System.out.print("Enter conversationId: ");
        String convId = sc.nextLine().trim();
        Conversation conv = conversationManager.getConversation(convId);
        if (conv == null) {
            System.out.println("Conversation not found");
            return;
        }
        List<Message> all = conv.getAllMessages();
        if (all.isEmpty()) {
            System.out.println("No visible messages");
            return;
        }
        System.out.println("Messages (oldest → newest):");
        for (Message m : all) {
            System.out.printf("[%s] %s: %s%n", m.getFormattedTimestamp(), m.getSenderId(), m.getContent());
        }
    }

    private void sendMessageToUser(Scanner sc) {
        System.out.print("Enter recipient username: ");
        String username = sc.nextLine().trim();
        User other = userManager.getUserByUsername(username);
        if (other == null) {
            System.out.println("User not found");
            return;
        }
        System.out.print("Message: ");
        String content = sc.nextLine().trim();
        if (content.isEmpty()) {
            System.out.println("Empty message");
            return;
        }
        Message msg = conversationManager.sendMessage(currentUser.getUserId(), other.getUserId(), content);
        System.out.println("Sent: " + msg.getContent());
    }

    private void undoLastMessage(Scanner sc) {
        List<Conversation> convs = conversationManager.getUserConversations(currentUser.getUserId());
        if (convs.isEmpty()) {
            System.out.println("No conversations");
            return;
        }
        System.out.println("Choose conversation to undo in:");
        for (int i = 0; i < convs.size(); i++) {
            Conversation c = convs.get(i);
            System.out.printf("%d. convId=%s other=%s%n", i + 1, c.getConversationId(), c.getOtherParticipant(currentUser.getUserId()));
        }
        int idx = getIntInput(sc, "Choice: ") - 1;
        if (idx < 0 || idx >= convs.size()) {
            System.out.println("Invalid choice");
            return;
        }
        Conversation chosen = convs.get(idx);
        boolean undone = conversationManager.undoLastMessage(currentUser.getUserId(), chosen.getConversationId());
        System.out.println("Undo result: " + undone);
    }

    private void markConversationRead(Scanner sc) {
        System.out.print("Enter conversationId to mark as read: ");
        String convId = sc.nextLine().trim();
        conversationManager.markAsRead(currentUser.getUserId(), convId);
        System.out.println("Marked as read (if conversation exists)");
    }

    private int getIntInput(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = sc.nextLine().trim();
            try {
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.println("Invalid number, try again.");
            }
        }
    }
}
