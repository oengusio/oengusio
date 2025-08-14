package app.oengus.adapter.rest.dto.v2.users.request;

import app.oengus.adapter.rest.dto.v2.users.ConnectionDto;
import app.oengus.application.LanguageService;
import app.oengus.domain.OengusUser;
import com.neovisionaries.i18n.CountryCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Schema
public class UserUpdateRequest {
    @NotNull
    @Size(min = 3, max = 32)
    @Pattern(regexp = OengusUser.USERNAME_REGEX)
    @Schema(description = "username of the user, always lowercase.")
    private String username;

    @NotNull
    @Size(min = 1, max = 32)
    @Schema(description = "How the user is displayed on the website")
    private String displayName;

    @NotNull
    @Email
    @Schema(description = "Email address of the user")
    private String email;

    @Schema(description = "True if the user is 'active' and can perform actions on the website. False for deactivated accounts")
    private boolean enabled;

    @NotNull
    @Schema(description = "The preferred pronouns of this user")
    private List<String> pronouns = new ArrayList<>();

    @NotNull
    @Schema(description = "The languages that this user speaks")
    private List<String> languagesSpoken = new ArrayList<>();

    @Nullable
    @Size(max = 3)
    @Schema(description = "The country that this user resides in")
    private String country;

    @NotNull
    @Schema(description = "Connected accounts of this user")
    private List<ConnectionDto> connections = new ArrayList<>();

    @Nullable
    private String discordId;

    @Nullable
    private String twitchId;

    @Nullable
    private String patreonId;

    private boolean savedGamesPublic;

    // <editor-fold desc="validation" defaultstate="collapsed">
    @AssertTrue(message = "You must have at least one account synced")
    public boolean isAtLeastOneAccountSynchronized() {
        // ignore for disabled users
        if (!this.enabled) { // TODO: check if password hash is set
            return true;
        }

        return StringUtils.isNotEmpty(this.discordId) ||
            StringUtils.isNotEmpty(this.twitchId);
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

        return this.languagesSpoken.stream().allMatch(LanguageService::isSupportedLanguage);
    }
    // </editor-fold>
}
