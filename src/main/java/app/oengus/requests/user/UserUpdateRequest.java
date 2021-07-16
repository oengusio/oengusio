package app.oengus.requests.user;

import app.oengus.entity.model.SocialAccount;
import app.oengus.spring.model.Views;
import com.fasterxml.jackson.annotation.JsonView;
import com.neovisionaries.i18n.CountryCode;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import javax.validation.constraints.*;
import java.util.List;

public class UserUpdateRequest implements IUserRequest {

    @JsonView(Views.Public.class)
    @Size(min = 3, max = 32)
    @Pattern(regexp = USERNAME_REGEX)
    private String username;

    @JsonView(Views.Public.class)
    @Size(max = 32)
    private String usernameJapanese;

    @JsonView(Views.Public.class)
    private boolean enabled;

    @JsonView(Views.Internal.class)
    @Email
    private String mail;

    @Nullable
    @JsonView(Views.Internal.class)
    private String discordId;

    @Nullable
    @JsonView(Views.Internal.class)
    private String twitchId;

    @Nullable
    @JsonView(Views.Internal.class)
    private String twitterId;

    @Nullable
    @JsonView(Views.Internal.class)
    private String patreonId;

    @NotNull
    @JsonView(Views.Public.class)
    private List<SocialAccount> connections;

    @Nullable
    @JsonView(Views.Public.class)
    @Size(max = 20)
    private String pronouns;

    @Nullable
    @JsonView(Views.Public.class)
    @Size(max = 3)
    private String country;

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

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    @Nullable
    public String getDiscordId() {
        return discordId;
    }

    public void setDiscordId(@Nullable String discordId) {
        this.discordId = discordId;
    }

    @Nullable
    public String getTwitchId() {
        return twitchId;
    }

    public void setTwitchId(@Nullable String twitchId) {
        this.twitchId = twitchId;
    }

    @Nullable
    public String getTwitterId() {
        return twitterId;
    }

    public void setTwitterId(@Nullable String twitterId) {
        this.twitterId = twitterId;
    }

    @Nullable
    public String getPatreonId() {
        return patreonId;
    }

    public void setPatreonId(@Nullable String patreonId) {
        this.patreonId = patreonId;
    }

    @Nullable
    public String getPronouns() {
        return pronouns;
    }

    public void setPronouns(@Nullable String pronouns) {
        this.pronouns = pronouns;
    }

    public List<SocialAccount> getConnections() {
        return connections;
    }

    public void setConnections(List<SocialAccount> connections) {
        this.connections = connections;
    }

    @Nullable
    public String getCountry() {
        return country;
    }

    public void setCountry(@Nullable String country) {
        this.country = country;
    }

    /// <editor-fold desc="validation" defaultstate="collapsed">
    @AssertTrue(message = "You must have at least one account synced")
    public boolean isAtLeastOneAccountSynchronized() {
        // ignore for disabled users
        if (!this.enabled) {
            return true;
        }

        return StringUtils.isNotEmpty(this.discordId) ||
            StringUtils.isNotEmpty(this.twitchId) ||
            StringUtils.isNotEmpty(this.twitterId);
    }

    @AssertTrue(message = "The country code is not valid")
    public boolean isCountryValid() {
        if (this.country == null || this.country.isBlank()) {
            return true;
        }

        final CountryCode byCode = CountryCode.getByCode(this.country);

        return byCode != null && byCode != CountryCode.UNDEFINED;
    }
    /// </editor-fold>
}
