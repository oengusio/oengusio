package app.oengus.exception;

public class OengusBusinessException extends RuntimeException {

	public OengusBusinessException() {
	}

	public OengusBusinessException(final String message) {
		super(message);
	}
}
