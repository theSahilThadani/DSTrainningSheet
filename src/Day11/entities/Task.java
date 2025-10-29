package Day11.entities;
import java.time.LocalDateTime;
import java.util.*;

public final class Task {
    private final String id;
    private final String name;
    private final String description;
    private final TaskPriority priority;
    private final Set<String> dependencies;
    private final String assignedTo;
    private final LocalDateTime deadline;
    private volatile TaskStatus status;
    private volatile LocalDateTime startedAt;
    private volatile LocalDateTime completedAt;
    private volatile long executionTimeMs;
    private volatile String failureReason;

    //constructor to create new TASK
    public Task(String id, String name, String description, TaskPriority priority,
                Set<String> dependencies, String assignedTo, LocalDateTime deadline) {
        validateInput(id, name, assignedTo);

        this.id = id;
        this.name = name;
        this.description = description;
        this.priority = priority != null ? priority : TaskPriority.MEDIUM;
        this.dependencies = dependencies != null ? new HashSet<>(dependencies) : new HashSet<>();
        this.assignedTo = assignedTo;
        this.deadline = deadline;
        this.status = TaskStatus.PENDING;
    }

    // validator
    private void validateInput(String id, String name, String assignedTo) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Task ID cannot be empty");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Task name cannot be empty");
        }
        if (assignedTo == null || assignedTo.trim().isEmpty()) {
            throw new IllegalArgumentException("Task must be assigned to a team member");
        }
    }


    //Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public TaskPriority getPriority() { return priority; }
    public Set<String> getDependencies() { return new HashSet<>(dependencies); }
    public String getAssignedTo() { return assignedTo; }
    public LocalDateTime getDeadline() { return deadline; }
    public TaskStatus getStatus() { return status; }
    public LocalDateTime getStartedAt() { return startedAt; }
    public LocalDateTime getCompletedAt() { return completedAt; }
    public long getExecutionTimeMs() { return executionTimeMs; }
    public String getFailureReason() { return failureReason; }


    // Check if task has any dependencies.
    public boolean hasDependencies() {
        return !dependencies.isEmpty();
    }

    // Check if task depends on specific task.
    public boolean dependsOn(String taskId) {
        return dependencies.contains(taskId);
    }

    //Check if task is ready to execute.
    public boolean isReady() {
        return status == TaskStatus.READY;
    }

    //Check if task has passed deadline.
    public boolean isOverdue() {
        if (deadline == null) return false;
        return LocalDateTime.now().isAfter(deadline);
    }

    // Mark task as started.
    public void markStarted() {
        this.status = TaskStatus.IN_PROGRESS;
        this.startedAt = LocalDateTime.now();
    }

    // mark task as completed
    public void markCompleted() {
        this.status = TaskStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
        if (startedAt != null) {
            this.executionTimeMs = java.time.temporal.ChronoUnit.MILLIS.between(startedAt, completedAt);
        }
    }

    // mark task as failed with reason
    public void markFailed(String reason) {
        this.status = TaskStatus.FAILED;
        this.failureReason = reason;
        this.completedAt = LocalDateTime.now();
    }

    // mark task as cancelled
    public void markCancelled() {
        this.status = TaskStatus.CANCELLED;
    }

    // Update task status based on dependencies.
    public void setReady() {
        if (status == TaskStatus.PENDING && dependencies.isEmpty()) {
            this.status = TaskStatus.READY;
        }
    }

    public String toString() {
        return String.format(
                "Task{id='%s', name='%s', priority=%s, status=%s, assigned=%s, deadline=%s}",
                id, name, priority, status, assignedTo, deadline
        );
    }
}