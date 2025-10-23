package Day6_7.entities;

public enum ConversationType {
    ONE_TO_ONE("💬 Private Chat"),
    GROUP("👥 Group Chat");

    private final String displayName;

    ConversationType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
