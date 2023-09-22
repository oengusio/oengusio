package app.oengus.entity.dto.v2.auth;

import app.oengus.entity.dto.v2.users.ConnectionDto;

import java.util.List;

public class SignUpDto {
    private String displayName;
    private String username;
    private List<String> pronouns = List.of();
    private List<String> languagesSpoken = List.of();
    private String email;
    private String password;
    private List<ConnectionDto> connections;

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<String> getPronouns() {
        return pronouns;
    }

    public void setPronouns(List<String> pronouns) {
        this.pronouns = pronouns;
    }

    public List<String> getLanguagesSpoken() {
        return languagesSpoken;
    }

    public void setLanguagesSpoken(List<String> languagesSpoken) {
        this.languagesSpoken = languagesSpoken;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<ConnectionDto> getConnections() {
        return connections;
    }

    public void setConnections(List<ConnectionDto> connections) {
        this.connections = connections;
    }
}
