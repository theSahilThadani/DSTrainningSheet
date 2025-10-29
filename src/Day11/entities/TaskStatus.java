package Day11.entities;

public enum TaskStatus {
    PENDING("Waiting for execution"),
    READY("Dependencies met, ready to execute"),
    IN_PROGRESS("Currently being executed"),
    COMPLETED("Successfully executed"),
    FAILED("Execution failed"),
    CANCELLED("Task cancelled"),
    BLOCKED("Blocked by dependencies");

    private final String description;

    TaskStatus(String description) {
        this.description = description;
    }

    public String getDescription() { return description; }
}