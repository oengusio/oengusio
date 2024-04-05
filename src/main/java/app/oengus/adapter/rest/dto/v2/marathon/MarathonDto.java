package app.oengus.adapter.rest.dto.v2.marathon;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.annotation.Nullable;
import javax.validation.constraints.*;
import java.time.Duration;
import java.time.ZonedDateTime;

import static app.oengus.entity.dto.UserDto.MASTODON_REGEX;

@Schema
public class MarathonDto {

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
    @Size(max = 5000)
    @Schema(required = true, description = "The description is what is shown to users when they visit this marathon's homepage")
    private String description;

    @NotNull
    @Schema(required = true, description = "Marathon privacy, marathon will not show on homepage and in calendar if set to false")
    private boolean isPrivate;

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
    private boolean onSite;

    private String location;
    private String country;

    @NotNull
    @Schema(required = true, description = "The language that this marathon is in. ISO 639-1 language codes only.")
    private String language = "en";

    private int maxGamesPerRunner = 5;
    private int maxCategoriesPerGame = 3;
    private boolean allowMultiplayer = true;
    private int maxNumberOfScreens = 4;
    private boolean videoRequired = true;
    private boolean allowEmulators = true;
    private boolean discordRequired = false;
    // TODO: can the user configure this field?
    private boolean canEditSubmissions = false;

    // Could paywall these ;)
    // For real tho, these are not cheap to have
    private boolean unlimitedGames = false;
    private boolean unlimitedCategories = false;

    private String twitch;
    private String twitter;
    private String mastodon;
    private String discord;
    private String youtube;
    private boolean hideDiscord;

    @NotNull
    @Schema(
        required = true,
        description = "The default length for the setup time field. This field is using the ISO-8601 duration format.",
        example = "PT30M"
    )
    private Duration defaultSetupTime;

    private boolean selectionDone;
    private boolean scheduleDone;

    // incentive information (do we care about that in this request?)
    private boolean donationsOpen = false;
    private boolean hasIncentives = false;
    private boolean hasDonations = false;
    private String payee;
    private String supportedCharity;
    private String donationCurrency;

    // TODO: split between bot settings and webhook settings
    private String webhook;
    private boolean announceAcceptedSubmissions = false;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean aPrivate) {
        isPrivate = aPrivate;
    }

    public ZonedDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(ZonedDateTime startDate) {
        this.startDate = startDate;
    }

    public ZonedDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(ZonedDateTime endDate) {
        this.endDate = endDate;
    }

    @Nullable
    public ZonedDateTime getSubmissionsStartDate() {
        return submissionsStartDate;
    }

    public void setSubmissionsStartDate(@Nullable ZonedDateTime submissionsStartDate) {
        this.submissionsStartDate = submissionsStartDate;
    }

    @Nullable
    public ZonedDateTime getSubmissionsEndDate() {
        return submissionsEndDate;
    }

    public void setSubmissionsEndDate(@Nullable ZonedDateTime submissionsEndDate) {
        this.submissionsEndDate = submissionsEndDate;
    }

    public boolean isOnSite() {
        return onSite;
    }

    public void setOnSite(boolean onSite) {
        this.onSite = onSite;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public int getMaxGamesPerRunner() {
        return maxGamesPerRunner;
    }

    public void setMaxGamesPerRunner(int maxGamesPerRunner) {
        this.maxGamesPerRunner = maxGamesPerRunner;
    }

    public int getMaxCategoriesPerGame() {
        return maxCategoriesPerGame;
    }

    public void setMaxCategoriesPerGame(int maxCategoriesPerGame) {
        this.maxCategoriesPerGame = maxCategoriesPerGame;
    }

    public boolean isAllowMultiplayer() {
        return allowMultiplayer;
    }

    public void setAllowMultiplayer(boolean allowMultiplayer) {
        this.allowMultiplayer = allowMultiplayer;
    }

    public int getMaxNumberOfScreens() {
        return maxNumberOfScreens;
    }

    public void setMaxNumberOfScreens(int maxNumberOfScreens) {
        this.maxNumberOfScreens = maxNumberOfScreens;
    }

    public boolean isVideoRequired() {
        return videoRequired;
    }

    public void setVideoRequired(boolean videoRequired) {
        this.videoRequired = videoRequired;
    }

    public boolean isAllowEmulators() {
        return allowEmulators;
    }

    public void setAllowEmulators(boolean allowEmulators) {
        this.allowEmulators = allowEmulators;
    }

    public boolean isDiscordRequired() {
        return discordRequired;
    }

    public void setDiscordRequired(boolean discordRequired) {
        this.discordRequired = discordRequired;
    }

    public boolean isCanEditSubmissions() {
        return canEditSubmissions;
    }

    public void setCanEditSubmissions(boolean canEditSubmissions) {
        this.canEditSubmissions = canEditSubmissions;
    }

    public boolean isUnlimitedGames() {
        return unlimitedGames;
    }

    public void setUnlimitedGames(boolean unlimitedGames) {
        this.unlimitedGames = unlimitedGames;
    }

    public boolean isUnlimitedCategories() {
        return unlimitedCategories;
    }

    public void setUnlimitedCategories(boolean unlimitedCategories) {
        this.unlimitedCategories = unlimitedCategories;
    }

    public String getTwitch() {
        return twitch;
    }

    public void setTwitch(String twitch) {
        this.twitch = twitch;
    }

    public String getTwitter() {
        return twitter;
    }

    public void setTwitter(String twitter) {
        this.twitter = twitter;
    }

    public String getDiscord() {
        return discord;
    }

    public void setDiscord(String discord) {
        this.discord = discord;
    }

    public String getYoutube() {
        return youtube;
    }

    public void setYoutube(String youtube) {
        this.youtube = youtube;
    }

    public boolean isHideDiscord() {
        return hideDiscord;
    }

    public void setHideDiscord(boolean hideDiscord) {
        this.hideDiscord = hideDiscord;
    }

    public String getDefaultSetupTime() {
        return defaultSetupTime.toString();
    }

    public void setDefaultSetupTime(String defaultSetupTime) {
        this.defaultSetupTime = Duration.parse(defaultSetupTime);
    }

    public boolean isSelectionDone() {
        return selectionDone;
    }

    public void setSelectionDone(boolean selectionDone) {
        this.selectionDone = selectionDone;
    }

    public boolean isScheduleDone() {
        return scheduleDone;
    }

    public void setScheduleDone(boolean scheduleDone) {
        this.scheduleDone = scheduleDone;
    }

    public boolean isDonationsOpen() {
        return donationsOpen;
    }

    public void setDonationsOpen(boolean donationsOpen) {
        this.donationsOpen = donationsOpen;
    }

    public boolean isHasIncentives() {
        return hasIncentives;
    }

    public void setHasIncentives(boolean hasIncentives) {
        this.hasIncentives = hasIncentives;
    }

    public boolean isHasDonations() {
        return hasDonations;
    }

    public void setHasDonations(boolean hasDonations) {
        this.hasDonations = hasDonations;
    }

    public String getPayee() {
        return payee;
    }

    public void setPayee(String payee) {
        this.payee = payee;
    }

    public String getSupportedCharity() {
        return supportedCharity;
    }

    public void setSupportedCharity(String supportedCharity) {
        this.supportedCharity = supportedCharity;
    }

    public String getDonationCurrency() {
        return donationCurrency;
    }

    public void setDonationCurrency(String donationCurrency) {
        this.donationCurrency = donationCurrency;
    }

    public String getWebhook() {
        return webhook;
    }

    public void setWebhook(String webhook) {
        this.webhook = webhook;
    }

    public boolean isAnnounceAcceptedSubmissions() {
        return announceAcceptedSubmissions;
    }

    public void setAnnounceAcceptedSubmissions(boolean announceAcceptedSubmissions) {
        this.announceAcceptedSubmissions = announceAcceptedSubmissions;
    }

    @JsonIgnore
    @AssertTrue(message = "Mastodon instance is not in the valid format")
    public boolean isMastodonValid() {
        return this.mastodon == null || this.mastodon.matches(MASTODON_REGEX);
    }
}
