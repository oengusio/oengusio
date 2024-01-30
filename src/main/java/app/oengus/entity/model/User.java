package app.oengus.entity.model;

import app.oengus.entity.IUsername;
import app.oengus.spring.model.Role;
import app.oengus.spring.model.Views;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.annotation.Nullable;
import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import static app.oengus.entity.dto.UserDto.PRONOUN_REGEX;
import static app.oengus.entity.dto.UserDto.USERNAME_REGEX;

// TODO: add created time so we can delete accounts 1 year days of not verifying the email.

@Entity
@Getter
@Setter
@Table(name = "users")
public class User implements UserDetails, IUsername {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(Views.Public.class)
    private Integer id;

    @Column
    @JsonView(Views.Public.class)
    @Size(min = 3, max = 32)
    @Pattern(regexp = USERNAME_REGEX)
    private String username;

    @Column(name = "display_name")
    @JsonView(Views.Public.class)
    @Size(max = 32)
    private String displayName;

    @Column(name = "active")
    @JsonView(Views.Public.class)
    private boolean enabled;

    @ElementCollection
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    @JsonView(Views.Public.class)
    private List<Role> roles;

    @JsonManagedReference
    @OrderBy("platform ASC")
    @JsonView(Views.Public.class)
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SocialAccount> connections;

    @Email
    @Column
    @JsonView(Views.Internal.class)
    private String mail;

    @JsonView(Views.NeverFuckingShow.class)
    @Column(name = "hashed_password")
    private String hashedPassword;

    @Column(name = "email_verified")
    @JsonView(Views.Public.class)
    private boolean emailVerified;

    @Column(name = "discord_id")
    @JsonView(Views.Internal.class)
    private String discordId;

    @Column(name = "twitch_id")
    @JsonView(Views.Internal.class)
    private String twitchId;

    @Column(name = "twitter_id")
    @JsonView(Views.Internal.class)
    private String twitterId;

    @Column(name = "patreon_id")
    @JsonView(Views.Internal.class)
    private String patreonId;

    @Nullable
    @Column(name = "pronouns")
    @JsonView(Views.Public.class)
    @Size(max = 255)
    @Pattern(regexp = PRONOUN_REGEX)
    private String pronouns;

    @Nullable
    @Size(max = 3)
    @Column(name = "country")
    @JsonView(Views.Public.class)
    private String country;

    @Nullable
    @Column(name = "languages_spoken")
    @JsonView(Views.Public.class)
    private String languagesSpoken;

    @Column(name = "mfa_enabled")
    @JsonView(Views.Internal.class)
    private boolean mfaEnabled;

    @Nullable
    @Column(name = "mfa_secret")
    @JsonView(Views.NeverFuckingShow.class)
    private String mfaSecret;

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    // TODO: I hate v1, this should be a DTO
    @JsonView(Views.Public.class)
    public boolean hasPassword() {
        return StringUtils.isNotEmpty(this.hashedPassword);
    }

    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles.stream()
            .map(Enum::toString)
            .map(SimpleGrantedAuthority::new)
            .toList();
    }

    @Override
    @JsonIgnore
    public String getPassword() {
        return hashedPassword;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return !roles.contains(Role.ROLE_BANNED);
    }

    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @JsonIgnore
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

    @JsonIgnore
    @AssertTrue
    public boolean isEmailPresentForExistingUser() {
        if (this.id != null && this.enabled) {
            return StringUtils.isNotEmpty(this.mail);
        }

        return true;
    }

    @JsonIgnore
    public String getHashedPassword() {
        return hashedPassword;
    }

    @JsonIgnore
    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    public void setConnections(List<SocialAccount> connections) {
        if (this.connections == null) {
            this.connections = new ArrayList<>();
        }

        this.connections.clear();
        this.connections.addAll(connections);
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
}
