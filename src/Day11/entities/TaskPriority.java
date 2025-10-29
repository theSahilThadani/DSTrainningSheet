package Day11.entities;

public enum TaskPriority {
    CRITICAL(4, "Critical - Must execute immediately"),
    HIGH(3, "High - Execute soon"),
    MEDIUM(2, "Medium - Execute when available"),
    LOW(1, "Low - Execute when idle");

    private final int value;
    private final String description;

    TaskPriority(int value, String description) {
        this.value = value;
        this.description = description;
    }

    public int getValue() { return value; }
    public String getDescription() { return description; }

    public int compareWith(TaskPriority other) {
        return Integer.compare(this.value, other.value);
    }
}