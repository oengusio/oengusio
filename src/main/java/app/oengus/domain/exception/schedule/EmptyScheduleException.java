package app.oengus.domain.exception.schedule;

import app.oengus.domain.exception.OengusBusinessException;

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
