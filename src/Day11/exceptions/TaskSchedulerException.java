package Day11.exceptions;


public class TaskSchedulerException extends RuntimeException {
    public TaskSchedulerException(String message) {
        super(message);
    }

    public TaskSchedulerException(String message, Throwable cause) {
        super(message, cause);
    }
}


class DeadlineViolationException extends TaskSchedulerException {
    public DeadlineViolationException(String taskId) {
        super("Deadline violated for task: " + taskId);
    }
}