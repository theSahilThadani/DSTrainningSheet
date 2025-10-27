package Day9.entities;

public enum CartStatus {
    ACTIVE("Active"),
    ABANDONED("Abandoned"),
    CHECKED_OUT("Checked Out"),
    EXPIRED("Expired");

    private final String displayName;

    CartStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() { return displayName; }
}