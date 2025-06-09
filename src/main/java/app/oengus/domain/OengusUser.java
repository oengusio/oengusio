package app.oengus.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// TODO: should this be a record?
@Getter
@Setter
@EqualsAndHashCode
@RequiredArgsConstructor
public class OengusUser implements IUsername {
    public static final String DISCORD_USERNAME_REGEX = "^\\S.{0,30}\\S\\s*(?:#\\d{4})?$";
    public static final String EMAIL_REGEX = "^(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9]))\\.){3}(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9])|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])$";
    public static final String BLUESKY_USERNAME_REGEX = "(@([a-zA-Z0-9]([a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?\\.)+[a-zA-Z]([a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)$";
    public static final String MASTODON_REGEX = "^@?\\b([A-Za-z0-9._%+-]+)@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})\\b$";
    public static final String USERNAME_REGEX = "^[\\w\\-0-9]{3,32}$";
    public static final String PRONOUN_REGEX = "^[\\w\\/,]+$";
    public static final String SPEEDRUN_COM_NAME_REGEX = "^[\\w\\.\\-À-Üà-øoù-ÿŒœ]{1,20}$";

    private final int id;
    private String username;
    private String displayName;
    private String email; // TODO: use response model to hide fields we don't want.
    private String password; // hashed password
    private boolean enabled;
    private Set<Role> roles = new HashSet<>(); // Can't have duplicate roles.
    private List<Connection> connections = new ArrayList<>();
    private boolean emailVerified;
    private List<String> pronouns = new ArrayList<>();
    private String country;
    private List<String> languagesSpoken = new ArrayList<>();
    private boolean mfaEnabled;
    private String mfaSecret;
    private ZonedDateTime createdAt;
    private ZonedDateTime lastLogin;
    private boolean needsPasswordReset;

    private String patreonId;
    private String discordId;
    private String twitchId;
    private String twitterId;

    // TODO: does lombok have a way of doing this?

    public void addRole(Role role) {
        this.roles.add(role);
    }

    public void removeRole(Role role) {
        this.roles.remove(role);
    }

    public void addConnection(Connection connection) {
        this.connections.add(connection);
    }

    public void removeConnection(Connection connection) {
        this.connections.remove(connection);
    }

    public void addPronoun(String pronoun) {
        this.pronouns.add(pronoun);
    }

    public void removePronoun(String pronoun) {
        this.pronouns.remove(pronoun);
    }

    public void addLanguage(String language) {
        this.languagesSpoken.add(language);
    }

    public void removeLanguage(String language) {
        this.languagesSpoken.remove(language);
    }
}
