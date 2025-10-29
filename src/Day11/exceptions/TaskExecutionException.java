package Day11.exceptions;

public class TaskExecutionException extends TaskSchedulerException {
    public TaskExecutionException(String taskId, String reason) {
        super("Task execution failed [" + taskId + "]: " + reason);
    }
}
