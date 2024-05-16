package app.oengus.domain.exception.auth;

import lombok.Getter;

@Getter
public class UnknownUserException extends RuntimeException {
    private final String username;
    private final String email;

    public UnknownUserException(String username) {
        this(username, null);
    }

    public UnknownUserException(String username, String email) {
        super("User not found: " + username);

        this.username = username;
        this.email = email;
    }
}
