package Day6_7.entities;

public class UserInfo {
    private final String userId;
    private final String username;
    private final String displayName;
    private final UserStatus status;
    private final long lastSeen;

    public UserInfo(String userId, String username, String displayName,
                    UserStatus status, long lastSeen) {
        this.userId = userId;
        this.username = username;
        this.displayName = displayName;
        this.status = status;
        this.lastSeen = lastSeen;
    }

    // Getters
    public String getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getDisplayName() { return displayName; }
    public UserStatus getStatus() { return status; }
    public long getLastSeen() { return lastSeen; }

    @Override
    public String toString() {
        return String.format("%s (@%s) - %s", displayName, username, status);
    }
}
