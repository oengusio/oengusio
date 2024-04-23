package app.oengus.adapter.rest.dto;

import app.oengus.adapter.jpa.entity.TeamEntity;
import app.oengus.adapter.rest.dto.v1.V1QuestionDto;
import app.oengus.adapter.rest.dto.v1.V1UserDto;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class MarathonDto {
    private String id;
    private String name;
    private V1UserDto creator;
    private ZonedDateTime startDate;
    private ZonedDateTime endDate;
    private ZonedDateTime submissionsStartDate;
    private ZonedDateTime submissionsEndDate;
    private String description;
    private String location;
    private String language;
    private int maxGamesPerRunner;
    private int maxCategoriesPerGame;
    private boolean hasMultiplayer;
    private int maxNumberOfScreens;
    private String twitch;
    private String twitter;
    private String mastodon;
    private String discord;
    private String country;
    private boolean discordPrivacy;
    private boolean submitsOpen;
    private Duration defaultSetupTime;
    private boolean selectionDone;
    private boolean scheduleDone;
    private boolean donationsOpen;
    private boolean isPrivate;
    private boolean onsite;
    private boolean videoRequired;
    private boolean unlimitedGames;
    private boolean unlimitedCategories;
    private boolean emulatorAuthorized;
    private List<V1UserDto> moderators;
    private boolean hasIncentives;
    private boolean canEditSubmissions;
    private List<V1QuestionDto> questions = new ArrayList<>();
    private boolean hasDonations;
    private String payee;
    private String supportedCharity;
    private String donationCurrency;
    private String webhook;
    private String youtube;
    private String discordGuildId;
    private String discordGuildName;
    private boolean discordRequired;
    private boolean announceAcceptedSubmissions;
    private String userInfoHidden;
    // TODO: team model
    private List<TeamEntity> teams;


    private BigDecimal donationsTotal;
	private boolean hasSubmitted;

    public boolean getIsPrivate() {
        return this.isPrivate;
    }
}
