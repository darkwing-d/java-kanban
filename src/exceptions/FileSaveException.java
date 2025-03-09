package exceptions;

public class FileSaveException extends RuntimeException {
  
    public FileSaveException(String message, Throwable cause) {
        super(message, cause);
    }
}
