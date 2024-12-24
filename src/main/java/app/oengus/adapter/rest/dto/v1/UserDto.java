package app.oengus.adapter.rest.dto.v1;

import app.oengus.adapter.jpa.entity.SocialAccount;
import app.oengus.adapter.rest.Views;
import app.oengus.application.LanguageService;
import app.oengus.domain.IUsername;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.neovisionaries.i18n.CountryCode;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import jakarta.validation.constraints.*;
import java.util.Arrays;
import java.util.List;

@Getter
@Setter
// TODO: this needs to be renamed
// TODO: move regexes to domain
public class UserDto implements IUsername {
    @JsonIgnore
    public static final String DISCORD_USERNAME_REGEX = "^\\S.{0,30}\\S\\s*(?:#\\d{4})?$";
    @JsonIgnore
    public static final String EMAIL_REGEX = "^(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9]))\\.){3}(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9])|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])$";
    @JsonIgnore
    public static final String BLUESKY_USERNAME_REGEX = "(@([a-zA-Z0-9]([a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?\\.)+[a-zA-Z]([a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)$";
    @JsonIgnore
    public static final String MASTODON_REGEX = "^@?[\\w\\-]{3,32}@[^.]+.\\w+$";
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
    private String email;

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

    @Deprecated
    public String getUsernameJapanese() {
        return displayName;
    }

    // <editor-fold desc="validation" defaultstate="collapsed">
    @AssertTrue(message = "You must have at least one account synced")
    public boolean isAtLeastOneAccountSynchronized() {
        // ignore for disabled users
        if (!this.enabled) { // TODO: check if password hash is set
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

    // TODO: fix
    /*@JsonIgnore
    @AssertTrue
    public boolean isEmailPresentForExistingUser() {
        if (this.id != null && this.enabled) {
            return StringUtils.isNotEmpty(this.mail);
        }

        return true;
    }*/
    // </editor-fold>
}
