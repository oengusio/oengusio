package app.oengus.adapter.rest.dto.v1.request;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.time.DurationMin;

import javax.annotation.Nullable;
import javax.validation.constraints.*;
import java.time.Duration;
import java.time.ZonedDateTime;

import static app.oengus.adapter.rest.dto.v1.UserDto.MASTODON_REGEX;

@Getter
@Setter
public class MarathonUpdateRequestDto {
    @Size(min = 4, max = 40)
    @Pattern(regexp = "^[\\w\\- \\p{L}]{4,40}$")
    private String name;

    @NotNull
    private ZonedDateTime startDate;

    @NotNull
    private ZonedDateTime endDate;

    @Nullable
    private ZonedDateTime submissionsStartDate;

    @Nullable
    private ZonedDateTime submissionsEndDate;

    @Size(max = 5000)
    private String description;

    @Size(max = 150)
    private String location;

    private Boolean onsite;

    @Size(max = 3)
    private String country;

    private String language;

    @Min(value = 1)
    private Integer maxGamesPerRunner;

    @Min(value = 1)
    @Max(value = 10)
    private Integer maxCategoriesPerGame;

    @Min(value = 1)
    private Integer maxNumberOfScreens;

    @Size(max = 15)
    private String twitter;

    @Size(max = 25)
    private String twitch;

    @Size(max = 255)
    private String mastodon;

    @Size(max = 100)
    private String youtube;

    @Size(max = 20)
    private String discord;

    private Boolean discordPrivacy;

    private Boolean hasMultiplayer;

    private Boolean submitsOpen;

    @DurationMin(minutes = 1)
    private Duration defaultSetupTime;

    private Boolean selectionDone;

    private Boolean isPrivate;

    private Boolean videoRequired;

    private Boolean unlimitedGames;

    private Boolean unlimitedCategories;

    private Boolean emulatorAuthorized;

    private Boolean canEditSubmissions;

    private String webhook;

    private String discordGuildId;
    private String discordGuildName;

    private Boolean discordRequired;

    private Boolean announceAcceptedSubmissions;

    @AssertTrue(message = "The end date must be after the start date")
    public boolean endDateIsAfterStartDate() {
        return this.endDate.isAfter(this.startDate);
    }

    @AssertTrue(message = "The submission end date must be after the submission start date")
    public boolean submissionEndDateIsAfterSubmissionStartDate() {
        if (this.submissionsEndDate == null || this.submissionsStartDate == null) {
            return true;
        }

        return this.submissionsEndDate.isAfter(this.submissionsStartDate);
    }

    @AssertTrue(message = "Mastodon instance is not in the valid format")
    public boolean isMastodonValid() {
        return this.mastodon == null || this.mastodon.isBlank() || this.mastodon.matches(MASTODON_REGEX);
    }
}
