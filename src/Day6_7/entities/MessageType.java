package Day6_7.entities;

public enum MessageType {
    TEXT("Text"),
    IMAGE("Image"),
    FILE("File"),
    SYSTEM("System");

    private final String displayName;

    MessageType(String displayName) {
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
