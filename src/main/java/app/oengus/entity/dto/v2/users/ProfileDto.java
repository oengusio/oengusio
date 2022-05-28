package app.oengus.entity.dto.v2.users;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@Schema
public class ProfileDto {

    @Schema(description = "The unique id of this user")
    private int id;

    @Schema(description = "The unique username of this user")
    private String username;

    @Nullable
    @Schema(description = "The username that should be displayed when the site is switched to japanese")
    private String usernameJapanese;

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

    // TODO: add connections


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

    @Nullable
    public String getUsernameJapanese() {
        return usernameJapanese;
    }

    public void setUsernameJapanese(@Nullable String usernameJapanese) {
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
}
