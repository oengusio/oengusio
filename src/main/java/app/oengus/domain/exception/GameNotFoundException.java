package app.oengus.domain.exception;

import app.oengus.domain.exception.base.GenericNotFoundException;

public class GameNotFoundException extends GenericNotFoundException {
    public GameNotFoundException() {
        super("Game not found");
    }
}
