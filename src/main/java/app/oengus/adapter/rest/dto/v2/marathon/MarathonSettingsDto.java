package app.oengus.adapter.rest.dto.v2.marathon;

import app.oengus.domain.OengusUser;
import app.oengus.domain.marathon.Marathon;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.time.DurationMin;

import javax.annotation.Nullable;
import jakarta.validation.constraints.*;
import java.time.Duration;
import java.time.ZonedDateTime;

@Schema
@Getter
@Setter
public class MarathonSettingsDto {

    @NotNull(message = "The marathon id must not be null")
    @Size(min = 4, max = 10)
    @Pattern(regexp = "^[\\w\\-]{4,10}$")
    @Schema(required = true, description = "The id of this marathon. Will display in urls referencing this marathon")
    private String id;

    @NotNull(message = "The marathon name must not be null")
    @Size(min = 4, max = 40)
    @Pattern(regexp = "^[\\w\\- ]{4,40}$")
    @Schema(required = true, description = "The name of this marathon")
    private String name;

    @NotNull(message = "The marathon description must not be null, empty strings are allowed however")
    @Size(max = Marathon.MAX_DESC_LENGTH)
    @Schema(required = true, description = "The description is what is shown to users when they visit this marathon's homepage")
    private String description;

    @NotNull
    @Schema(required = true, description = "Marathon privacy, marathon will not show on homepage and in calendar if set to false")
    private Boolean isPrivate;

    @NotNull(message = "Marathon start date must not be null")
    @FutureOrPresent(message = "The start date must be the current date or a future date")
    @Schema(required = true, description = "The date and time of when this marathon starts")
    private ZonedDateTime startDate;

    @Future(message = "The end date must be a future date")
    @NotNull(message = "Marathon end date must not be null")
    @Schema(required = true, description = "The date and time of when this marathon ends")
    private ZonedDateTime endDate;

    @Nullable
    @FutureOrPresent(message = "Submissions cannot open in the past, must be current or future date")
    @Schema(description = "Allows Oengus to automatically open the submissions for this marathon on the specified date")
    private ZonedDateTime submissionsStartDate;

    @Nullable
    @Future(message = "Submissions can only end in the future :)")
    @Schema(description = "Allows Oengus to automatically close the submissions for this marathon on the specified date")
    private ZonedDateTime submissionsEndDate;

    @Schema(required = true, description = "On-site vs online marathon, true to mark this marathon as on-site")
    private Boolean onsite;

    @Size(max = 150)
    private String location;

    @Size(max = 3)
    private String country;

    @NotNull
    @Schema(required = true, description = "The language that this marathon is in. ISO 639-1 language codes only.")
    private String language = "en";

    @Min(value = 1)
    private Integer maxGamesPerRunner = 5;

    @Min(value = 1)
    @Max(value = 10)
    private Integer maxCategoriesPerGame = 3;
    private Boolean allowMultiplayer = true;

    @Min(value = 1)
    private Integer maxNumberOfScreens = 4;
    private Boolean videoRequired = true;
    private Boolean allowEmulators = true;
    private Boolean discordRequired = false;
    private String discordGuildId;
    private String discordGuildName;
    // TODO: can the user configure this field?
//    private Boolean canEditSubmissions = false;
    private Boolean submissionsOpen = false;

    // Could paywall these ;)
    // For real tho, these are not cheap to have
    private Boolean unlimitedGames = false;
    private Boolean unlimitedCategories = false;

    private String twitch;
    private String twitter;
    private String mastodon;
    private String discord;
    private String youtube;
    private Boolean discordPrivate;

    @NotNull
    @Schema(
        required = true,
        description = "The default length for the setup time field. This field is using the ISO-8601 duration format.",
        example = "PT30M"
    )
    @DurationMin(minutes = 1)
    private Duration defaultSetupTime;

    private Boolean selectionDone;
    private Boolean scheduleDone;

    // incentive information (do we care about that in this request?)
    private Boolean donationsOpen = false;
    private Boolean hasIncentives = false;
    private Boolean hasDonations = false;
    private String payee;
    private String supportedCharity;
    private String donationCurrency;

    // TODO: split between bot settings and webhook settings
    private String webhook;
    private Boolean announceAcceptedSubmissions = false;

    @JsonIgnore
    @AssertTrue(message = "The end date must be after the start date")
    public boolean endDateIsAfterStartDate() {
        return this.endDate.isAfter(this.startDate);
    }

    @JsonIgnore
    @AssertTrue(message = "The submission end date must be after the submission start date")
    public boolean submissionEndDateIsAfterSubmissionStartDate() {
        if (this.submissionsEndDate == null || this.submissionsStartDate == null) {
            return true;
        }

        return this.submissionsEndDate.isAfter(this.submissionsStartDate);
    }

    @JsonIgnore
    @AssertTrue(message = "Mastodon instance is not in the valid format")
    public boolean isMastodonValid() {
        return this.mastodon == null || this.mastodon.matches(OengusUser.MASTODON_REGEX);
    }
}
