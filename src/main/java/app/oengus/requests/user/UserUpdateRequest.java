package app.oengus.requests.user;

import app.oengus.spring.model.Views;
import com.fasterxml.jackson.annotation.JsonView;

import javax.persistence.Column;
import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class UserUpdateRequest implements IUserRequest {

    @JsonView(Views.Public.class)
    @Size(min = 3, max = 32)
    @Pattern(regexp = USERNAME_REGEX)
    private String username;

    @JsonView(Views.Public.class)
    @Size(max = 32)
    private String usernameJapanese;

    @JsonView(Views.Public.class)
    private boolean enabled;

    @JsonView(Views.Internal.class)
    @Email
    private String mail;

    @JsonView(Views.Internal.class)
    private String discordId;

    @JsonView(Views.Internal.class)
    private String twitchId;

    @Column(name = "twitter_id")
    @JsonView(Views.Internal.class)
    private String twitterId;

    @JsonView(Views.Public.class)
    @Size(max = 37)
    private String discordName;

    @JsonView(Views.Public.class)
    @Size(max = 15)
    private String twitterName;

    @JsonView(Views.Public.class)
    @Size(max = 25)
    private String twitchName;

    @JsonView(Views.Public.class)
    @Size(max = 20)
    @Pattern(regexp = SPEEDRUN_COM_NAME_REGEX)
    private String speedruncomName;
}
