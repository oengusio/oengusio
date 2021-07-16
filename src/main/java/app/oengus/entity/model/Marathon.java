package app.oengus.entity.model;

import app.oengus.spring.model.Views;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonView;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.validator.constraints.time.DurationMin;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.List;

@Entity
@Table(name = "marathon")
@JsonIgnoreProperties(ignoreUnknown = true)
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Marathon {

    @Id
    @JsonView(Views.Public.class)
    @Size(min = 4, max = 10)
    @Pattern(regexp = "^[\\w\\-]{4,10}$")
    private String id;

    @Column(name = "name")
    @JsonView(Views.Public.class)
    @Size(min = 4, max = 40)
    @Pattern(regexp = "^[\\w\\- ]{4,40}$")
    private String name;

    @ManyToOne
    @JsonView(Views.Public.class)
    @JoinColumn(name = "creator_id")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @NotNull
    private User creator;

    @Column(name = "start_date")
    @JsonView(Views.Public.class)
    @NotNull
    private ZonedDateTime startDate;

    @Column(name = "end_date")
    @JsonView(Views.Public.class)
    @NotNull
    private ZonedDateTime endDate;

    @Column(name = "submissions_start_date")
    @JsonView(Views.Public.class)
    private ZonedDateTime submissionsStartDate;

    @Column(name = "submissions_end_date")
    @JsonView(Views.Public.class)
    private ZonedDateTime submissionsEndDate;

    @Column(name = "description")
    @JsonView(Views.Public.class)
    @Size(max = 5000)
    private String description;

    @Column(name = "is_onsite")
    @JsonView(Views.Public.class)
    private boolean onsite = false;

    @Column(name = "location")
    @JsonView(Views.Public.class)
    @Size(max = 150)
    private String location;

    @Column(name = "language")
    @JsonView(Views.Public.class)
    private String language = "en";

    @Column(name = "max_games_per_runner")
    @JsonView(Views.Public.class)
    @Min(value = 1)
    private int maxGamesPerRunner = 5;

    @Column(name = "max_categories_per_game")
    @JsonView(Views.Public.class)
    @Min(value = 1)
    @Max(value = 10)
    private int maxCategoriesPerGame = 3;

    @Column(name = "has_multiplayer")
    @JsonView(Views.Public.class)
    private boolean hasMultiplayer = true;

    @Column(name = "max_number_of_screens")
    @JsonView(Views.Public.class)
    @Min(value = 1)
    private int maxNumberOfScreens = 4;

    @Column(name = "twitch")
    @JsonView(Views.Public.class)
    @Size(max = 25)
    private String twitch;

    @Column(name = "twitter")
    @JsonView(Views.Public.class)
    @Size(max = 15)
    private String twitter;

    @Column(name = "discord")
    @JsonView(Views.Public.class)
    @Size(max = 20)
    private String discord;

    @Column(name = "country")
    @JsonView(Views.Public.class)
    @Size(max = 3)
    private String country;

    @Column(name = "discord_privacy")
    @JsonView(Views.Public.class)
    private boolean discordPrivacy = false;

    @Column(name = "submits_open")
    @JsonView(Views.Public.class)
    private boolean submitsOpen = false;

    @Column(name = "default_setup_time")
    @JsonView(Views.Public.class)
    @DurationMin(seconds = 1)
    private Duration defaultSetupTime;

    @Column(name = "is_selection_done")
    @JsonView(Views.Public.class)
    private boolean selectionDone = false;

    @Column(name = "schedule_done")
    @JsonView(Views.Public.class)
    private boolean scheduleDone = false;

    @Column(name = "cleared")
    @JsonIgnore
    private boolean cleared = false;

    @Column(name = "donation_open")
    @JsonView(Views.Public.class)
    private boolean donationsOpen = true;

    @Column(name = "is_private")
    @JsonView(Views.Public.class)
    private boolean isPrivate = false;

    @Column(name = "video_required")
    @JsonView(Views.Public.class)
    private boolean videoRequired = true;

    @Column(name = "unlimited_games")
    @JsonView(Views.Public.class)
    private boolean unlimitedGames = false;

    @Column(name = "unlimited_categories")
    @JsonView(Views.Public.class)
    private boolean unlimitedCategories = false;

    @Column(name = "emulator_authorized")
    @JsonView(Views.Public.class)
    private boolean emulatorAuthorized = true;

    @ManyToMany
    @JoinTable(
            name = "moderator",
            joinColumns = {@JoinColumn(name = "marathon_id")},
            inverseJoinColumns = {@JoinColumn(name = "user_id")}
    )
    @OrderBy(value = "id ASC")
    @JsonView(Views.Public.class)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private List<User> moderators;

    @Column(name = "incentives")
    @JsonView(Views.Public.class)
    private boolean hasIncentives = false;

    @Column(name = "can_edit_submissions")
    @JsonView(Views.Public.class)
    private boolean canEditSubmissions = false;

    @OneToMany(mappedBy = "marathon", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference(value = "marathonReference")
    @OrderBy("position ASC")
    @JsonView(Views.Public.class)
    private List<Question> questions;

    @Column(name = "has_donations")
    @JsonView(Views.Public.class)
    private boolean hasDonations = false;

    @Column(name = "donation_payee")
    @JsonView(Views.Public.class)
    @Size(max = 100)
    private String payee;

    @Column(name = "supported_charity")
    @JsonView(Views.Public.class)
    @Size(max = 100)
    private String supportedCharity;

    @Column(name = "donation_currency_iso")
    @JsonView(Views.Public.class)
    @Size(max = 3)
    private String donationCurrency;

    @Column(name = "webhook")
    @JsonView(Views.Public.class)
    @Size(max = 200)
    private String webhook;

    @Column(name = "youtube")
    @JsonView(Views.Public.class)
    @Size(max = 100)
    private String youtube;

    @Column(name = "discord_guild_id")
    @JsonView(Views.Public.class)
    @Size(max = 20)
    private String discordGuildId;

    @Column(name = "discord_guild_name")
    @JsonView(Views.Public.class)
    @Size(max = 100)
    private String discordGuildName;

    @Column(name = "discord_required")
    @JsonView(Views.Public.class)
    private boolean discordRequired = false;

    @Column(name = "announce_accepted_submissions")
    @JsonView(Views.Public.class)
    private boolean announceAcceptedSubmissions = false;

    @Column(name = "applications_open")
    @JsonView(Views.Public.class)
    private boolean applicationsOpen = false;

    public String getId() {
        return this.id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public User getCreator() {
        return this.creator;
    }

    public void setCreator(final User creator) {
        this.creator = creator;
    }

    public ZonedDateTime getStartDate() {
        return this.startDate;
    }

    public void setStartDate(final ZonedDateTime startDate) {
        this.startDate = startDate;
    }

    public ZonedDateTime getEndDate() {
        return this.endDate;
    }

    public void setEndDate(final ZonedDateTime endDate) {
        this.endDate = endDate;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public boolean getOnsite() {
        return this.onsite;
    }

    public void setOnsite(final boolean onsite) {
        this.onsite = onsite;
    }

    public String getLocation() {
        return this.location;
    }

    public void setLocation(final String location) {
        this.location = location;
    }

    public String getLanguage() {
        return this.language;
    }

    public void setLanguage(final String language) {
        this.language = language;
    }

    public int getMaxGamesPerRunner() {
        return this.maxGamesPerRunner;
    }

    public void setMaxGamesPerRunner(final int maxGamesPerRunner) {
        this.maxGamesPerRunner = maxGamesPerRunner;
    }

    public int getMaxCategoriesPerGame() {
        return this.maxCategoriesPerGame;
    }

    public void setMaxCategoriesPerGame(final int maxCategoriesPerGame) {
        this.maxCategoriesPerGame = maxCategoriesPerGame;
    }

    public boolean getHasMultiplayer() {
        return this.hasMultiplayer;
    }

    public void setHasMultiplayer(final boolean hasMultiplayer) {
        this.hasMultiplayer = hasMultiplayer;
    }

    public int getMaxNumberOfScreens() {
        return this.maxNumberOfScreens;
    }

    public void setMaxNumberOfScreens(final int maxNumberOfScreens) {
        this.maxNumberOfScreens = maxNumberOfScreens;
    }

    public String getTwitch() {
        return this.twitch;
    }

    public void setTwitch(final String twitch) {
        this.twitch = twitch;
    }

    public String getTwitter() {
        return this.twitter;
    }

    public void setTwitter(final String twitter) {
        this.twitter = twitter;
    }

    public String getDiscord() {
        return this.discord;
    }

    public void setDiscord(final String discord) {
        this.discord = discord;
    }

    public List<User> getModerators() {
        return this.moderators;
    }

    public void setModerators(final List<User> moderators) {
        this.moderators = moderators;
    }

    public String getCountry() {
        return this.country;
    }

    public void setCountry(final String country) {
        this.country = country;
    }

    public boolean getDiscordPrivacy() {
        return this.discordPrivacy;
    }

    public void setDiscordPrivacy(final boolean discordPrivacy) {
        this.discordPrivacy = discordPrivacy;
    }

    public boolean isSubmitsOpen() {
        return this.submitsOpen;
    }

    public void setSubmitsOpen(final boolean submitsOpen) {
        this.submitsOpen = submitsOpen;
    }

    public Duration getDefaultSetupTime() {
        return this.defaultSetupTime;
    }

    public void setDefaultSetupTime(final Duration defaultSetupTime) {
        this.defaultSetupTime = defaultSetupTime;
    }

    public boolean isSelectionDone() {
        return this.selectionDone;
    }

    public void setSelectionDone(final boolean selectionDone) {
        this.selectionDone = selectionDone;
    }

    public boolean isScheduleDone() {
        return this.scheduleDone;
    }

    public void setScheduleDone(final boolean scheduleDone) {
        this.scheduleDone = scheduleDone;
    }

    public boolean getIsPrivate() {
        return this.isPrivate;
    }

    public void setIsPrivate(final boolean aPrivate) {
        this.isPrivate = aPrivate;
    }

    public boolean isHasIncentives() {
        return this.hasIncentives;
    }

    public void setHasIncentives(final boolean hasIncentives) {
        this.hasIncentives = hasIncentives;
    }

    public List<Question> getQuestions() {
        return this.questions;
    }

    public void setQuestions(final List<Question> questions) {
        this.questions = questions;
    }

    public boolean isCanEditSubmissions() {
        return this.canEditSubmissions;
    }

    public void setCanEditSubmissions(final boolean canEditSubmissions) {
        this.canEditSubmissions = canEditSubmissions;
    }

    public boolean isHasDonations() {
        return this.hasDonations;
    }

    public String getPayee() {
        return this.payee;
    }

    public void setPayee(final String payee) {
        this.payee = payee;
    }

    public String getDonationCurrency() {
        return this.donationCurrency;
    }

    public void setDonationCurrency(final String donationCurrency) {
        this.donationCurrency = donationCurrency;
    }

    public String getSupportedCharity() {
        return this.supportedCharity;
    }

    public void setSupportedCharity(final String supportedCharity) {
        this.supportedCharity = supportedCharity;
    }

    public void setHasDonations(final boolean hasDonations) {
        this.hasDonations = hasDonations;
    }

    public boolean isCleared() {
        return this.cleared;
    }

    public void setCleared(final boolean cleared) {
        this.cleared = cleared;
    }

    public String getWebhook() {
        return this.webhook;
    }

    public void setWebhook(final String donationWebhook) {
        this.webhook = donationWebhook;
    }

    public boolean isDonationsOpen() {
        return this.donationsOpen;
    }

    public void setDonationsOpen(final boolean donationsOpen) {
        this.donationsOpen = donationsOpen;
    }

    public boolean isVideoRequired() {
        return this.videoRequired;
    }

    public void setVideoRequired(final boolean videoRequired) {
        this.videoRequired = videoRequired;
    }

    public boolean isUnlimitedGames() {
        return this.unlimitedGames;
    }

    public void setUnlimitedGames(final boolean unlimitedGames) {
        this.unlimitedGames = unlimitedGames;
    }

    public boolean isUnlimitedCategories() {
        return this.unlimitedCategories;
    }

    public void setUnlimitedCategories(final boolean unlimitedCategories) {
        this.unlimitedCategories = unlimitedCategories;
    }

    public boolean isEmulatorAuthorized() {
        return this.emulatorAuthorized;
    }

    public void setEmulatorAuthorized(final boolean emulatorAuthorized) {
        this.emulatorAuthorized = emulatorAuthorized;
    }

    public String getYoutube() {
        return this.youtube;
    }

    public void setYoutube(final String youtube) {
        this.youtube = youtube;
    }

    public String getDiscordGuildId() {
        return discordGuildId;
    }

    public void setDiscordGuildId(String discordGuildId) {
        this.discordGuildId = discordGuildId;
    }

    public String getDiscordGuildName() {
        return discordGuildName;
    }

    public void setDiscordGuildName(String discordGuildName) {
        this.discordGuildName = discordGuildName;
    }

    public boolean isDiscordRequired() {
        return discordRequired;
    }

    public void setDiscordRequired(boolean discordRequired) {
        this.discordRequired = discordRequired;
    }

    public ZonedDateTime getSubmissionsStartDate() {
        return this.submissionsStartDate;
    }

    public void setSubmissionsStartDate(final ZonedDateTime submissionsStartDate) {
        this.submissionsStartDate = submissionsStartDate;
    }

    public ZonedDateTime getSubmissionsEndDate() {
        return this.submissionsEndDate;
    }

    public void setSubmissionsEndDate(final ZonedDateTime submissionsEndDate) {
        this.submissionsEndDate = submissionsEndDate;
    }

    public boolean hasWebhook() {
        return StringUtils.isNotEmpty(this.getWebhook());
    }

    public boolean isAnnounceAcceptedSubmissions() {
        return announceAcceptedSubmissions;
    }

    public void setAnnounceAcceptedSubmissions(boolean announceAcceptedRuns) {
        this.announceAcceptedSubmissions = announceAcceptedRuns;
    }

    public boolean isApplicationsOpen() {
        return applicationsOpen;
    }

    public void setApplicationsOpen(boolean applications_open) {
        this.applicationsOpen = applications_open;
    }
}
