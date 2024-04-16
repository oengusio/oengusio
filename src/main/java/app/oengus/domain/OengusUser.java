package app.oengus.domain;

import app.oengus.spring.model.Role;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// TODO: should this be a record?
@Getter
@Setter
@RequiredArgsConstructor
public class OengusUser implements IUsername {
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
