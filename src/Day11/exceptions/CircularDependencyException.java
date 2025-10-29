package Day11.exceptions;

public class CircularDependencyException extends TaskSchedulerException {
    public CircularDependencyException(String message) {
        super("Circular dependency detected: " + message);
    }
}
