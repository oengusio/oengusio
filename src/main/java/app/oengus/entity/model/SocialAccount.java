package app.oengus.entity.model;

import app.oengus.entity.constants.SocialPlatform;
import app.oengus.spring.model.Views;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonView;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Cache;

import java.io.Serializable;
import java.util.Objects;

import static app.oengus.requests.user.IUserRequest.*;

@SuppressWarnings("unused")
@Entity
@Table(name = "social_accounts")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class SocialAccount implements Serializable {
    @Id
    @JsonView(Views.Public.class)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference
    @JoinColumn(name = "user_id")
    @JsonView(Views.Internal.class)
    private User user;

    @NotNull
    @Column(name = "platform")
    @Enumerated(EnumType.STRING) /* Default is ORDINAL */
    @JsonView(Views.Public.class)
    private SocialPlatform platform;

    // TODO: platform based validation
    @NotNull
    @Size(max = 320)
    @Column(name = "username")
    @JsonView(Views.Public.class)
    private String username;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @NotNull
    public User getUser() {
        return user;
    }

    public void setUser(@NotNull User user) {
        this.user = user;
    }

    @NotNull
    public SocialPlatform getPlatform() {
        return platform;
    }

    public void setPlatform(@NotNull SocialPlatform platform) {
        this.platform = platform;
    }

    @NotNull
    public String getUsername() {
        return username;
    }

    public void setUsername(@NotNull String username) {
        this.username = username;
    }

    @AssertTrue(message = "The username does not have a valid format for the platform")
    public boolean isUsernameValidForPlatform() {
        return switch (this.platform) {
            case SPEEDRUNCOM -> this.username.length() < 20 && this.username.matches(SPEEDRUN_COM_NAME_REGEX);
            case DISCORD -> this.username.matches(DISCORD_USERNAME_REGEX);
            case EMAIL -> this.username.matches(EMAIL_REGEX);
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
