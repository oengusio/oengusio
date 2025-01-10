package app.oengus.adapter.jpa.entity;

import app.oengus.domain.marathon.Marathon;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.time.DurationMin;

import javax.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.List;

import static jakarta.persistence.CascadeType.ALL;

@Getter
@Setter
@Entity
@Table(name = "marathon")
public class MarathonEntity {

    @Id
    @Size(min = 4, max = 10)
    @Pattern(regexp = "^[\\w\\-]{4,10}$")
    private String id;

    @Column(name = "name")
    @Size(min = 4, max = 40)
    @Pattern(regexp = "^[\\w\\- \\p{L}]{4,40}$")
    private String name;

    @ManyToOne
    @JoinColumn(name = "creator_id")
    @NotNull
    private User creator;

    @Column(name = "start_date")
    @NotNull
    private ZonedDateTime startDate;

    @Column(name = "end_date")
    @NotNull
    private ZonedDateTime endDate;

    @Column(name = "submissions_start_date")
    @Nullable
    private ZonedDateTime submissionsStartDate;

    @Column(name = "submissions_end_date")
    @Nullable
    private ZonedDateTime submissionsEndDate;

    @Column(name = "description")
    @Size(max = Marathon.MAX_DESC_LENGTH)
    private String description;

    @Column(name = "is_onsite")
    private boolean onsite = false;

    @Column(name = "location")
    @Size(max = 150)
    private String location;

    @Column(name = "language")
    private String language = "en";

    @Column(name = "max_games_per_runner")
    @Min(value = 1)
    private int maxGamesPerRunner = 5;

    @Column(name = "max_categories_per_game")
    @Min(value = 1)
    @Max(value = 10)
    private int maxCategoriesPerGame = 3;

    @Column(name = "has_multiplayer")
    private boolean hasMultiplayer = true;

    @Column(name = "max_number_of_screens")
    @Min(value = 1)
    private int maxNumberOfScreens = 4;

    @Column(name = "twitch")
    @Size(max = 25)
    private String twitch;

    @Column(name = "twitter")
    @Size(max = 15)
    private String twitter;

    @Nullable
    @Column(name = "mastodon")
    @Size(max = 255)
    private String mastodon;

    @Column(name = "discord")
    @Size(max = 20)
    private String discord;

    @Column(name = "country")
    @Size(max = 3)
    private String country;

    @Column(name = "discord_privacy")
    private boolean discordPrivacy = false;

    @Column(name = "submits_open")
    private boolean submitsOpen = false;

    @Column(name = "default_setup_time")
    @DurationMin(seconds = 1)
    private Duration defaultSetupTime;

    @Column(name = "is_selection_done")
    private boolean selectionDone = false;

    @Column(name = "schedule_done")
    private boolean scheduleDone = false;

    @Column(name = "cleared")
    private boolean cleared = false; // what the fuck is this?

    @Column(name = "donation_open")
    private boolean donationsOpen = true;

    @Column(name = "is_private")
    private boolean isPrivate = false;

    @Column(name = "video_required")
    private boolean videoRequired = true;

    @Column(name = "unlimited_games")
    private boolean unlimitedGames = false;

    @Column(name = "unlimited_categories")
    private boolean unlimitedCategories = false;

    @Column(name = "emulator_authorized")
    private boolean emulatorAuthorized = true;

    @ManyToMany
    @JoinTable(
            name = "moderator",
            joinColumns = {@JoinColumn(name = "marathon_id")},
            inverseJoinColumns = {@JoinColumn(name = "user_id")}
    )
    @OrderBy(value = "id ASC")
    private List<User> moderators;

    @Column(name = "incentives")
    private boolean hasIncentives = false;

    @Column(name = "can_edit_submissions")
    private boolean canEditSubmissions = false;

    @OneToMany(mappedBy = "marathon", cascade = ALL, orphanRemoval = true)
    @OrderBy("position ASC")
    private List<QuestionEntity> questions;

    @Column(name = "has_donations")
    private boolean hasDonations = false;

    @Column(name = "donation_payee")
    @Size(max = 100)
    private String payee;

    @Column(name = "supported_charity")
    @Size(max = 100)
    private String supportedCharity;

    @Column(name = "donation_currency_iso")
    @Size(max = 3)
    private String donationCurrency;

    @Column(name = "webhook")
    @Size(max = 200)
    private String webhook;

    @Column(name = "youtube")
    @Size(max = 100)
    private String youtube;

    @Column(name = "discord_guild_id")
    @Size(max = 20)
    private String discordGuildId;

    @Column(name = "discord_guild_name")
    @Size(max = 100)
    private String discordGuildName;

    @Column(name = "discord_required")
    private boolean discordRequired = false;

    @Column(name = "announce_accepted_submissions")
    private boolean announceAcceptedSubmissions = false;

    @Column(name = "user_info_hidden")
    private String userInfoHidden; // Fields of user info to hide?

    @OneToMany(mappedBy = "marathon", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TeamEntity> teams;

    public boolean hasWebhook() {
        return StringUtils.isNotEmpty(this.getWebhook());
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
