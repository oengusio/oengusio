package app.oengus.domain.exception;

public class WrappedException extends RuntimeException {
    public WrappedException(String message, Throwable cause) {
        super(message, cause);
    }
}
