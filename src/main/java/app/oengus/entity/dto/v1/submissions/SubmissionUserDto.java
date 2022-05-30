package app.oengus.entity.dto.v1.submissions;

import app.oengus.entity.model.SocialAccount;
import app.oengus.entity.model.User;
import app.oengus.spring.model.Role;

import java.util.List;

public class SubmissionUserDto {
    private int id;
    private String username;
    private String usernameJapanese;
    public boolean enabled;
    public List<String> pronouns;
    private String country;
    private List<String> languagesSpoken;
    private List<Role> roles;
    private List<SocialAccount> connections;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsernameJapanese() {
        return usernameJapanese;
    }

    public void setUsernameJapanese(String usernameJapanese) {
        this.usernameJapanese = usernameJapanese;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public List<String> getPronouns() {
        return pronouns;
    }

    public void setPronouns(List<String> pronouns) {
        this.pronouns = pronouns;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public List<String> getLanguagesSpoken() {
        return languagesSpoken;
    }

    public void setLanguagesSpoken(List<String> languagesSpoken) {
        this.languagesSpoken = languagesSpoken;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public List<SocialAccount> getConnections() {
        return connections;
    }

    public void setConnections(List<SocialAccount> connections) {
        this.connections = connections;
    }

    public static SubmissionUserDto fromUser(final User user) {
        final SubmissionUserDto model = new SubmissionUserDto();

        model.setId(user.getId());
        model.setUsername(user.getUsername());
        model.setUsernameJapanese(user.getUsernameJapanese());
        model.setEnabled(user.isEnabled());

        final String pronouns = user.getPronouns();

        if (pronouns == null) {
            model.setPronouns(List.of());
        } else {
            model.setPronouns(List.of(pronouns.split(",")));
        }

        model.setCountry(user.getCountry());

        final String languagesSpoken = user.getLanguagesSpoken();

        if (languagesSpoken == null) {
            model.setLanguagesSpoken(List.of());
        } else {
            model.setLanguagesSpoken(List.of(languagesSpoken.split(",")));
        }

        model.setRoles(user.getRoles());
        model.setConnections(user.getConnections());

        return model;
    }
}