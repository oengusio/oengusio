package app.oengus.domain.exception;

import app.oengus.domain.exception.base.GenericNotFoundException;

public class UserNotFoundException extends GenericNotFoundException {
    public UserNotFoundException() {
        super("User not found");
    }
}
