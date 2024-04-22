package app.oengus.adapter.jpa.entity;

import app.oengus.entity.model.Question;
import app.oengus.entity.model.TeamEntity;
import app.oengus.spring.model.Views;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.time.DurationMin;

import javax.annotation.Nullable;
import javax.persistence.*;
import javax.validation.constraints.*;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.List;

import static app.oengus.adapter.rest.dto.v1.UserDto.*;
import static javax.persistence.CascadeType.*;

@Getter
@Setter
@Entity
@Table(name = "marathon")
@JsonIgnoreProperties(ignoreUnknown = true)
public class MarathonEntity {

    @Id
    @JsonView(Views.Public.class)
    @Size(min = 4, max = 10)
    @Pattern(regexp = "^[\\w\\-]{4,10}$")
    private String id;

    @Column(name = "name")
    @JsonView(Views.Public.class)
    @Size(min = 4, max = 40)
    @Pattern(regexp = "^[\\w\\- \\p{L}]{4,40}$")
    private String name;

    @ManyToOne
    @JsonView(Views.Public.class)
    @JoinColumn(name = "creator_id")
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
    @Nullable
    private ZonedDateTime submissionsStartDate;

    @Column(name = "submissions_end_date")
    @JsonView(Views.Public.class)
    @Nullable
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

    @Nullable
    @Column(name = "mastodon")
    @JsonView(Views.Public.class)
    @Size(max = 255)
    private String mastodon;

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
    private boolean cleared = false; // what the fuck is this?

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
    private List<User> moderators;

    @Column(name = "incentives")
    @JsonView(Views.Public.class)
    private boolean hasIncentives = false;

    @Column(name = "can_edit_submissions")
    @JsonView(Views.Public.class)
    private boolean canEditSubmissions = false;

    @OneToMany(mappedBy = "marathon", cascade = ALL, orphanRemoval = true)
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

    @Column(name = "user_info_hidden")
    @JsonView(Views.Public.class)
    private String userInfoHidden; // Fields of user info to hide?

    @JsonManagedReference
    @JsonView(Views.Internal.class)
    @OneToMany(mappedBy = "marathon", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TeamEntity> teams;

    public boolean hasWebhook() {
        return StringUtils.isNotEmpty(this.getWebhook());
    }

    // Can't wait to drop v1 support
    @JsonIgnore
    @AssertTrue(message = "Mastodon instance is not in the valid format")
    public boolean isMastodonValid() {
        return this.mastodon == null || this.mastodon.isBlank() || this.mastodon.matches(MASTODON_REGEX);
    }

    /**
     * @param marathonId the id of the marathon
     * @return A fake marathon instance with this id
     */
    public static MarathonEntity ofId(String marathonId) {
        final MarathonEntity marathon = new MarathonEntity();
        marathon.setId(marathonId);

        return marathon;
    }
}
