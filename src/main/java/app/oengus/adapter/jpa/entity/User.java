package app.oengus.adapter.jpa.entity;

import app.oengus.domain.IUsername;
import app.oengus.domain.Role;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static app.oengus.adapter.rest.dto.v1.UserDto.USERNAME_REGEX;

@Entity
@Getter
@Setter
@Table(name = "users")
public class User implements IUsername {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    @Size(min = 3, max = 32)
    @Pattern(regexp = USERNAME_REGEX)
    private String username;

    @Column(name = "display_name")
    @Size(max = 32)
    private String displayName;

    @Column(name = "active")
    private boolean enabled;

    @ElementCollection
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    private List<Role> roles;

    @OrderBy("platform ASC")
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SocialAccount> connections;

    @Email
    @Column
    private String mail;

    @Column(name = "hashed_password")
    private String hashedPassword;

    @Column(name = "email_verified")
    private boolean emailVerified;

    @Column(name = "discord_id")
    private String discordId;

    @Column(name = "twitch_id")
    private String twitchId;

    @Column(name = "twitter_id")
    private String twitterId;

    @Column(name = "patreon_id")
    private String patreonId;

    @Nullable
    @Column(name = "pronouns")
    @Size(max = 255)
    private String pronouns;

    @Nullable
    @Size(max = 3)
    @Column(name = "country")
    private String country;

    @Nullable
    @Column(name = "languages_spoken")
    private String languagesSpoken;

    @Column(name = "mfa_enabled")
    private boolean mfaEnabled;

    @Nullable
    @Column(name = "mfa_secret")
    private String mfaSecret;

    @Column(name = "created_at")
    private ZonedDateTime createdAt;

    @Column(name = "last_login")
    private ZonedDateTime lastLogin;

    @Column(name = "needs_password_reset")
    private boolean needsPasswordReset;

    @AssertTrue
    public boolean isAtLeastOneAccountSynchronized() {
        // ignore for disabled users
        if (!this.enabled || StringUtils.isNotEmpty(this.hashedPassword)) {
            return true;
        }

        return StringUtils.isNotEmpty(this.discordId) ||
            StringUtils.isNotEmpty(this.twitchId) ||
            StringUtils.isNotEmpty(this.twitterId);
    }

    @AssertTrue
    public boolean isEmailPresentForExistingUser() {
        if (this.id != null && this.enabled) {
            return StringUtils.isNotEmpty(this.mail);
        }

        return true;
    }

    public void setConnections(List<SocialAccount> connections) {
        if (this.connections == null) {
            this.connections = new ArrayList<>();
        }

        this.connections.clear();
        this.connections.addAll(connections);
    }

    // TODO: better mappers
    public void setLanguagesSpoken(@Nullable String languagesSpoken) {
        this.languagesSpoken = languagesSpoken;
    }

    public void setLanguagesSpoken(@NotNull List<String> languagesSpoken) {
        this.languagesSpoken = String.join(",", languagesSpoken);
    }

    // TODO: better models, this should not be here
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return (id != null && id.equals(user.id)) && username.equals(user.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username);
    }

    public static User ofId(int id) {
        final var user = new User();

        user.setId(id);

        return user;
    }
}
