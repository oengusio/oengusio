package app.oengus.adapter.jpa.entity;

import app.oengus.domain.SocialPlatform;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Objects;

import static app.oengus.adapter.rest.dto.v1.UserDto.*;

@Getter
@Setter
@Entity
@Table(name = "social_accounts")
public class SocialAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @NotNull
    @Column(name = "platform")
    @Enumerated(EnumType.STRING) /* Default is ORDINAL */
    private SocialPlatform platform;

    @NotNull
    @Size(max = 320)
    @Column(name = "username")
    private String username;

    // TODO: pull out to DTO
    @JsonIgnore
    @AssertTrue(message = "The username does not have a valid format for the platform")
    public boolean isUsernameValidForPlatform() {
        return switch (this.platform) {
            case SPEEDRUNCOM -> this.username.length() < 20 && this.username.matches(SPEEDRUN_COM_NAME_REGEX);
            case DISCORD -> this.username.matches(DISCORD_USERNAME_REGEX);
            case EMAIL -> this.username.matches(EMAIL_REGEX);
            case MASTODON -> this.username.matches(MASTODON_REGEX);
            default -> this.username.matches(USERNAME_REGEX);
        };
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SocialAccount that = (SocialAccount) o;
        return platform == that.platform && username.equals(that.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(platform, username);
    }
}
