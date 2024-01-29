package app.oengus.application.exception;

public class WrappedException extends RuntimeException {
    public WrappedException(Throwable cause) {
        super(cause);
    }
}
