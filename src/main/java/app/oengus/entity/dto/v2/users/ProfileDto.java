package app.oengus.entity.dto.v2.users;

import app.oengus.entity.model.User;
import app.oengus.spring.model.Role;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@Schema
public class ProfileDto {

    @Schema(description = "The unique id of this user")
    private int id;

    @Schema(description = "The unique username of this user")
    private String username;

    @Schema(description = "The name that will be shown for the user in the interface")
    private String displayName;

    @Schema(description = "True if this profile is enabled, false otherwise")
    private boolean enabled;

    @Schema(description = "The preferred pronouns of this user")
    private List<String> pronouns = new ArrayList<>();

    @Schema(description = "The languages that this user speaks")
    private List<String> languagesSpoken = new ArrayList<>();

    @Schema(description = "True if this user is banned on the oengus platform")
    private boolean banned;

    @Nullable
    @Schema(description = "The country that this user resides in")
    private String country;

    @Schema(description = "Connected accounts of this user")
    private List<ConnectionDto> connections;

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

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(@Nonnull String displayName) {
        this.displayName = displayName;
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

    public void setPronouns(@Nullable List<String> pronouns) {
        this.pronouns = pronouns;
    }

    public List<String> getLanguagesSpoken() {
        return languagesSpoken;
    }

    public void setLanguagesSpoken(@Nullable List<String> languagesSpoken) {
        this.languagesSpoken = languagesSpoken;
    }

    public boolean isBanned() {
        return banned;
    }

    public void setBanned(boolean banned) {
        this.banned = banned;
    }

    @Nullable
    public String getCountry() {
        return country;
    }

    public void setCountry(@Nullable String country) {
        this.country = country;
    }

    public List<ConnectionDto> getConnections() {
        return connections;
    }

    public void setConnections(List<ConnectionDto> connections) {
        this.connections = connections;
    }

    public static ProfileDto fromUser(User user) {
        final ProfileDto profile = new ProfileDto();
        profile.setId(user.getId());
        profile.setUsername(user.getUsername());
        profile.setDisplayName(user.getDisplayName());
        profile.setEnabled(user.isEnabled());
        profile.setBanned(user.getRoles().contains(Role.ROLE_BANNED));

        final String pronouns = user.getPronouns();

        if (pronouns != null) {
            profile.setPronouns(
                List.of(pronouns.split(","))
            );
        }

        final String langs = user.getLanguagesSpoken();

        if (langs != null && !langs.isBlank()) {
            profile.setLanguagesSpoken(
                List.of(langs.split(","))
            );
        }

        profile.setConnections(
            user.getConnections()
                .stream()
                .map(ConnectionDto::from)
                .toList()
        );

        return profile;
    }
}
