package app.oengus.domain.exception;

import app.oengus.domain.SocialPlatform;

import java.util.Locale;

public class InvalidUsernameException extends RuntimeException {
    public InvalidUsernameException(SocialPlatform platform, String username) {
        super(
            "%s is not a valid username for %s".formatted(
                username, platform.name().toLowerCase(Locale.ROOT)
            )
        );
    }
}
