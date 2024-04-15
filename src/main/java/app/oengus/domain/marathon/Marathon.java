package app.oengus.domain.marathon;

import app.oengus.entity.model.Question;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
public class Marathon {
    private final String id;
    private final int creatorId;

    private String name;
    private ZonedDateTime startDate;
    private ZonedDateTime endDate;
    private ZonedDateTime submissionsStartDate;
    private ZonedDateTime submissionsEndDate;
    private String description;
    private boolean onsite = false;
    private String location;
    private String country;
    private String language = "en";
    private int maxGamesPerRunner = 5;
    private int maxCategoriesPerGame = 3;
    private boolean hasMultiplayer = true;
    private int maxNumberOfScreens = 4;
    private String twitch;
    private String twitter;
    private String mastodon;
    private String discord;
    private boolean discordPrivate = false;
    private boolean submissionsOpen = false;
    private Duration defaultSetupTime;
    private boolean selectionDone = false;
    private boolean scheduleDone = false;
    private boolean isPrivate = false;
    private boolean unlimitedGames = false;
    private boolean unlimitedCategories = false;
    private boolean emulatorAuthorized = true;
    private List<Integer> moderatorIds = new ArrayList<>();
    private boolean canEditSubmissions = false;
    // TODO: better model? Separate route?
    private List<Question> questions = new ArrayList<>();
    private String webhook;
    private String youtube;
    private boolean announceAcceptedSubmissions = false;

    // Submissions settings
    private boolean videoRequired = true;

    // Donation settings
    private boolean hasDonations = false;
    private boolean donationsOpen = true;
    private boolean hasIncentives = false;
    private String payee;
    private String supportedCharity;
    private String donationCurrency;

    // Discord settings
    private String discordGuildId;
    private String discordGuildName;
    private boolean discordRequired = false;

    // TODO: teams/volunteering settings

    public boolean hasWebhook() {
        return StringUtils.isNotEmpty(this.getWebhook());
    }
}
