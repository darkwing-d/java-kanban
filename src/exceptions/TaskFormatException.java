package exceptions;

public class TaskFormatException extends IllegalArgumentException {
    public TaskFormatException(String message) {
        super(message);
    }
}
