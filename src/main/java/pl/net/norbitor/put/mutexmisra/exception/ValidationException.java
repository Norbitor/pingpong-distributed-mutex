package pl.net.norbitor.put.mutexmisra.exception;

public class ValidationException extends Exception {
    public ValidationException() {
        super();
    }

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
