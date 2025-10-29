package Day11.exceptions;

public class DuplicateTaskException extends TaskSchedulerException {
    public DuplicateTaskException(String taskId) {
        super("Duplicate task ID: " + taskId);
    }
}
