package app.oengus.exception;

public class OengusBusinessException extends RuntimeException {

	public OengusBusinessException() {
	}

	public OengusBusinessException(final String message) {
		super(message);
	}

	public OengusBusinessException(final String message, final Throwable cause) {
		super(message, cause);
	}
}
