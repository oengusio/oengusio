package app.oengus.domain.exception;

public class OengusBusinessException extends RuntimeException {

	public OengusBusinessException() {
        super();
	}

	public OengusBusinessException(final String message) {
		super(message);
	}

	public OengusBusinessException(final String message, final Throwable cause) {
		super(message, cause);
	}
}
