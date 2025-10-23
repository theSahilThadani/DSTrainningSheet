package Day6_7.entities;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class User {
    private final String userId;
    private final String username;          // Immutable (like email)
    private String displayName;             // Mutable
    private final String passwordHash;      // BCrypt or similar
    private UserStatus status;
    private long lastSeen;
    private final long registeredAt;
    private final Set<String> conversations;

    public User(String userId, String username, String displayName, String passwordHash) {
        // Validation
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("UserId cannot be empty");
        }
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        if (!isValidUsername(username)) {
            throw new IllegalArgumentException("Username must be 3-20 alphanumeric characters");
        }
        if (passwordHash == null || passwordHash.isEmpty()) {
            throw new IllegalArgumentException("Password hash cannot be empty");
        }

        this.userId = userId;
        this.username = username.toLowerCase(); // Normalize
        this.displayName = displayName != null ? displayName : username;
        this.passwordHash = passwordHash;
        this.status = UserStatus.OFFLINE;
        this.registeredAt = System.currentTimeMillis();
        this.lastSeen = this.registeredAt;
        this.conversations = new HashSet<>();
    }

    private boolean isValidUsername(String username) {
        return username.matches("^[a-zA-Z0-9_]{3,20}$");
    }

    // Getters
    public String getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getDisplayName() { return displayName; }
    public UserStatus getStatus() { return status; }
    public long getLastSeen() { return lastSeen; }
    public long getRegisteredAt() { return registeredAt; }
    public Set<String> getConversations() { return new HashSet<>(conversations); }

    public boolean verifyPassword(String passwordHash) {
        // In production, use: BCrypt.checkpw(password, this.passwordHash)
        return this.passwordHash.equals(passwordHash);
    }

    public synchronized void updateStatus(UserStatus newStatus) {
        this.status = newStatus;
        this.lastSeen = System.currentTimeMillis();
    }

    public synchronized void setDisplayName(String displayName) {
        if (displayName == null || displayName.trim().isEmpty()) {
            throw new IllegalArgumentException("Display name cannot be empty");
        }
        this.displayName = displayName.trim();
    }

    public synchronized void addConversation(String conversationId) {
        conversations.add(conversationId);
    }

    public synchronized void removeConversation(String conversationId) {
        conversations.remove(conversationId);
    }

    /**
     * Get user info (without sensitive data)
     */
    public UserInfo toUserInfo() {
        return new UserInfo(userId, username, displayName, status, lastSeen);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return userId.equals(user.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }

    @Override
    public String toString() {
        return String.format("User{id='%s', username='%s', status=%s}",
                userId, username, status);
    }


}
