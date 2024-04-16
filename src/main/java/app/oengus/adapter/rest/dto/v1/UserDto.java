package app.oengus.adapter.rest.dto.v1;

import app.oengus.domain.IUsername;
import app.oengus.entity.model.SocialAccount;
import app.oengus.service.LanguageService;
import app.oengus.spring.model.Views;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.neovisionaries.i18n.CountryCode;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import javax.validation.constraints.*;
import java.util.Arrays;
import java.util.List;

// TODO: this needs to be renamed
public class UserDto implements IUsername {
    @JsonIgnore
    public static final String DISCORD_USERNAME_REGEX = "^\\S.{0,30}\\S\\s*(?:#\\d{4})?$";
    @JsonIgnore
    public static final String EMAIL_REGEX = "^(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9]))\\.){3}(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9])|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])$";
    @JsonIgnore
    public static final String MASTODON_REGEX = "^[\\w\\-]{3,32}@[^.]+.\\w+$";
    @JsonIgnore
    public static final String USERNAME_REGEX = "^[\\w\\-0-9]{3,32}$";
    @JsonIgnore
    public static final String PRONOUN_REGEX = "^[\\w\\/,]+$";
    @JsonIgnore
    public static final String SPEEDRUN_COM_NAME_REGEX = "^[\\w\\.\\-À-Üà-øoù-ÿŒœ]{0,20}$";

    @JsonView(Views.Public.class)
    @Size(min = 3, max = 32)
    @Pattern(regexp = USERNAME_REGEX)
    private String username;

    @JsonView(Views.Public.class)
    // Japanese users can have one character in their username
    @Size(min = 1, max = 32)
    private String displayName;

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
    @Size(max = 255)
    @Pattern(regexp = PRONOUN_REGEX)
    private String pronouns;

    @Nullable
    @JsonView(Views.Public.class)
    @Size(max = 3)
    private String country;

    @Nullable
    private String languagesSpoken;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Deprecated
    public String getUsernameJapanese() {
        return displayName;
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

    public String getLanguagesSpoken() {
        return languagesSpoken;
    }

    public void setLanguagesSpoken(String languagesSpoken) {
        this.languagesSpoken = languagesSpoken;
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

    @AssertTrue(message = "One of the languages in languages_spoken is not supported by Oengus")
    public boolean isLanguagesSpokenValid() {
        if (this.languagesSpoken == null || this.languagesSpoken.isEmpty()) {
            return true;
        }

        return Arrays.stream(this.languagesSpoken.split(",")).allMatch(LanguageService::isSupportedLanguage);
    }
    /// </editor-fold>
}
