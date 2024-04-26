package app.oengus.domain.exception.schedule;

import app.oengus.domain.exception.base.GenericNotFoundException;

public class ScheduleNotFoundException extends GenericNotFoundException {
    public ScheduleNotFoundException() {
        super("Schedule not found");
    }
}
