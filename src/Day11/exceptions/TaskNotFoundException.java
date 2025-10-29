package Day11.exceptions;

public class TaskNotFoundException extends TaskSchedulerException {
    public TaskNotFoundException(String taskId) {
        super("Task not found: " + taskId);
    }
}
