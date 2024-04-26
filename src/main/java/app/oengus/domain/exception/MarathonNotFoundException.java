package app.oengus.domain.exception;

import app.oengus.domain.exception.base.GenericNotFoundException;

public class MarathonNotFoundException extends GenericNotFoundException {
    public MarathonNotFoundException() {
        super("Marathon not found");
    }
}
