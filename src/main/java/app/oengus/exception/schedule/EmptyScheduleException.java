package app.oengus.exception.schedule;

import app.oengus.exception.OengusBusinessException;

public class EmptyScheduleException extends OengusBusinessException {
    public EmptyScheduleException() {
        super();
    }

    public EmptyScheduleException(String message) {
        super(message);
    }

    public EmptyScheduleException(String message, Throwable cause) {
        super(message, cause);
    }
}
