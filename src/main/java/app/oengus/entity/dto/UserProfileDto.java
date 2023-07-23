package app.oengus.entity.dto;

import app.oengus.entity.IUsername;
import app.oengus.entity.constants.SocialPlatform;
import app.oengus.entity.model.ScheduleLineRunner;
import app.oengus.entity.model.SocialAccount;
import app.oengus.entity.model.User;
import app.oengus.spring.model.Role;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class UserProfileDto implements IUsername {

    private int id;
    private boolean emailVerified;
    private String username;
    private String displayName;
    private boolean enabled;
    private List<SocialAccount> connections;
    private List<UserHistoryDto> history;
    private List<MarathonBasicInfoDto> moderatedMarathons;
    private List<UserApplicationHistoryDto> volunteeringHistory;
    @Nullable
    private String pronouns;
    @Nullable
    private String languagesSpoken;
    private boolean banned;
    private String country;

    private UserProfileDto() {
        this.history = new ArrayList<>();
        this.moderatedMarathons = new ArrayList<>();
        this.volunteeringHistory = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getUsernameJapanese() {
        return this.displayName;
    }

    public boolean getEnabled() {
        return this.enabled;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }

    public List<UserHistoryDto> getHistory() {
        return this.history;
    }

    public void setHistory(final List<UserHistoryDto> history) {
        this.history = history;
    }

    public List<MarathonBasicInfoDto> getModeratedMarathons() {
        return this.moderatedMarathons;
    }

    public void setModeratedMarathons(final List<MarathonBasicInfoDto> moderatedMarathons) {
        this.moderatedMarathons = moderatedMarathons;
    }

    public List<SocialAccount> getConnections() {
        return connections;
    }

    public void setConnections(List<SocialAccount> connections) {
        this.connections = connections;
    }

    @NotNull
    public String[] getPronouns() {
        if (this.pronouns == null || this.pronouns.isBlank()) {
            return new String[0];
        }

        return this.pronouns.split(",");
    }

    public void setPronouns(@Nullable String pronouns) {
        this.pronouns = pronouns;
    }

    @NotNull
    public String[] getLanguagesSpoken() {
        if (this.languagesSpoken == null || this.languagesSpoken.isBlank()) {
            return new String[0];
        }

        return this.languagesSpoken.split(",");
    }

    public void setLanguagesSpoken(@Nullable String languagesSpoken) {
        this.languagesSpoken = languagesSpoken;
    }

    public boolean isBanned() {
        return banned;
    }

    public void setBanned(boolean banned) {
        this.banned = banned;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public List<UserApplicationHistoryDto> getVolunteeringHistory() {
        return volunteeringHistory;
    }

    public void setVolunteeringHistory(List<UserApplicationHistoryDto> volunteeringHistory) {
        this.volunteeringHistory = volunteeringHistory;
    }

    ///// deprecated properties
    public String getTwitterName() {
        return this.getConnections().stream()
            .filter(
                (it) -> it.getPlatform() == SocialPlatform.TWITTER
            )
            .map(SocialAccount::getUsername)
            .findFirst()
            .orElse("");
    }

    public String getDiscordName() {
        return this.getConnections().stream()
            .filter(
                (it) -> it.getPlatform() == SocialPlatform.DISCORD
            )
            .map(SocialAccount::getUsername)
            .findFirst()
            .orElse("");
    }

    public String getTwitchName() {
        return this.getConnections().stream()
            .filter(
                (it) -> it.getPlatform() == SocialPlatform.TWITCH
            )
            .map(SocialAccount::getUsername)
            .findFirst()
            .orElse("");
    }

    public String getSpeedruncomName() {
        return this.getConnections().stream()
            .filter(
                (it) -> it.getPlatform() == SocialPlatform.SPEEDRUNCOM
            )
            .map(SocialAccount::getUsername)
            .findFirst()
            .orElse("");
    }

    public static UserProfileDto fromUserNoHistory(User user) {
        final var dto = new UserProfileDto();

        // Arrays get set within the constructor, we don't need to set them here as a result.
        dto.setId(user.getId());
        dto.setEmailVerified(user.isEmailVerified());
        dto.setUsername(user.getUsername());
        dto.setDisplayName(user.getDisplayName());
        dto.setEnabled(user.isEnabled());
        dto.setConnections(user.getConnections());
        dto.setPronouns(user.getPronouns());
        dto.setLanguagesSpoken(user.getLanguagesSpoken());
        dto.setBanned(user.getRoles().contains(Role.ROLE_BANNED));
        dto.setCountry(user.getCountry());

        return dto;
    }

    public static UserProfileDto fromScheduleLine(ScheduleLineRunner runner) {
        final var dto = new UserProfileDto();

        final User user = runner.getUser();

        if (user == null) {
            dto.setId(-1);
            dto.setUsername(runner.getRunnerName());
            dto.setDisplayName(runner.getRunnerName());
            dto.setEnabled(true);
            dto.setConnections(List.of());
            dto.setPronouns(null);
            dto.setLanguagesSpoken(null);
            dto.setBanned(false);
            dto.setCountry(null);
            return dto;
        }

        return fromUserNoHistory(user);
    }
}
