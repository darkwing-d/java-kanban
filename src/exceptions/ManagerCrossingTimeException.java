package exceptions;

public class ManagerCrossingTimeException extends RuntimeException {

    public ManagerCrossingTimeException(final String message) {
        super(message);
    }
}