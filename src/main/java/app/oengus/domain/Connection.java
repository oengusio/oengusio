package app.oengus.domain;

import app.oengus.domain.exception.InvalidUsernameException;
import lombok.Getter;
import lombok.Setter;

import static app.oengus.adapter.rest.dto.v1.UserDto.*;

@Getter
@Setter
public class Connection {
    private int id;
    private SocialPlatform platform;
    private String username;

    public Connection() {}

    public Connection(int id, SocialPlatform platform, String username) {
        this.id = id;
        this.platform = platform;

        if (!isUsernameValidForPlatform(username, this.platform)) {
            throw new InvalidUsernameException(this.platform, username);
        }

        this.username = username;
    }

    public static boolean isUsernameValidForPlatform(String username, SocialPlatform platform) {
        return switch (platform) {
            case BLUESKY -> username.matches(BLUESKY_USERNAME_REGEX);
            case SPEEDRUNCOM -> username.length() <= 20 && username.matches(SPEEDRUN_COM_NAME_REGEX);
            case DISCORD -> username.matches(DISCORD_USERNAME_REGEX);
            case EMAIL -> username.matches(EMAIL_REGEX);
            case MASTODON -> username.matches(MASTODON_REGEX);
            default -> username.matches(USERNAME_REGEX);
        };
    }
}
