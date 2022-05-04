package app.oengus.request.marathon;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.Duration;
import java.time.ZonedDateTime;

@ApiModel
public class MarathonCreateRequest {

    @NotNull
    @Size(min = 4, max = 10)
    @Pattern(regexp = "^[\\w\\-]{4,10}$")
    @ApiModelProperty(required = true, value = "The id of this marathon. Will display in urls referencing this marathon")
    private String id;

    @NotNull
    @Size(min = 4, max = 40)
    @Pattern(regexp = "^[\\w\\- ]{4,40}$")
    @ApiModelProperty(required = true, value = "The name of this marathon")
    private String name;

    @NotNull
    @Size(max = 5000)
    @ApiModelProperty(required = true, value = "The description is what is shown to users when they visit this marathon's homepage")
    private String description;

    @NotNull
    @ApiModelProperty(required = true, value = "Marathon privacy, marathon will not show on homepage and in calendar if set to false")
    private boolean isPrivate;

    private ZonedDateTime startDate;
    private ZonedDateTime endDate;
    private ZonedDateTime submissionsStartDate;
    private ZonedDateTime submissionsEndDate;

    private boolean onSite;
    private String location;
    private String country;
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
    private String discord;
    private String youtube;
    private boolean hideDiscord;

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
}
