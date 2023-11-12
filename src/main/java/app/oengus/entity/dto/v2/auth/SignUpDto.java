package app.oengus.entity.dto.v2.auth;

import app.oengus.entity.dto.UserDto;
import app.oengus.entity.dto.v2.users.ConnectionDto;
import app.oengus.service.LanguageService;
import app.oengus.validation.ValidPassword;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.neovisionaries.i18n.CountryCode;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.annotation.Nullable;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;

@Schema(description = "The data needed when a user signs up.")
public class SignUpDto {
    @Size(min = 1, max = 32)
    @Schema(description = "The name of the user that will be displayed on the website.")
    private String displayName;

    @Size(min = 3, max = 32)
    @Pattern(regexp = UserDto.USERNAME_REGEX)
    @Schema(description = "The unique username of the user.")
    private String username;

    @Nullable
    @Size(max = 3)
    @Schema(description = "The country code of the country that you currently live in.")
    private String country;

    @Schema(description = "The pronouns that the user has set. E.G. 'he/him','she/her', 'they/them'.")
    private List<String> pronouns = List.of();

    @Schema(description = "List of language codes that the user speaks. Language codes are validated.")
    private List<String> languagesSpoken = List.of();

    @Email
    @Schema(description = "The email of the user, a confirmation email will be sent. If not confirmed the account will be deleted after 30 days.")
    private String email;

    @ValidPassword
    @Schema(description = "The password of this new account.")
    private String password;

    @Schema(description = "List of connections that will be displayed on the user's profile page.")
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

    @Nullable
    public String getCountry() {
        return country;
    }

    public void setCountry(@Nullable String country) {
        this.country = country;
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

    // Validation below.

    @JsonIgnore
    @AssertTrue(message = "One of the language codes supplied is not supported.")
    public boolean areAllLanguageCodesValid() {
        return this.languagesSpoken.isEmpty() || LanguageService.areLanguagesSupported(this.languagesSpoken);
    }

    @JsonIgnore
    @AssertTrue(message = "The country code is not valid")
    public boolean isCountryValid() {
        if (this.country == null || this.country.isBlank()) {
            return true;
        }

        final CountryCode byCode = CountryCode.getByCode(this.country);

        return byCode != null && byCode != CountryCode.UNDEFINED;
    }
}