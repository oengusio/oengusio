package app.oengus.entity.dto;

import app.oengus.domain.IUsername;
import app.oengus.domain.SocialPlatform;
import app.oengus.entity.model.ScheduleLineRunner;
import app.oengus.entity.model.SocialAccount;
import app.oengus.adapter.jpa.entity.User;
import app.oengus.spring.model.Role;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class UserProfileDto implements IUsername {
    private int id;
    private boolean emailVerified;
    private String username;
    private String displayName;
    private boolean enabled;
    private List<SocialAccount> connections;
    private List<UserHistoryDto> history = new ArrayList<>();
    private List<MarathonBasicInfoDto> moderatedMarathons = new ArrayList<>();
    private List<UserApplicationHistoryDto> volunteeringHistory = new ArrayList<>();
    private List<String> pronouns = new ArrayList<>();
    private List<String> languagesSpoken = new ArrayList<>();
    private boolean banned;
    private String country;

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

    @Deprecated
    private static UserProfileDto fromUserNoHistory(User user) {
        final var dto = new UserProfileDto();

        // Arrays get set within the constructor, we don't need to set them here as a result.
        dto.setId(user.getId());
        dto.setEmailVerified(user.isEmailVerified());
        dto.setUsername(user.getUsername());
        dto.setDisplayName(user.getDisplayName());
        dto.setEnabled(user.isEnabled());
        dto.setConnections(user.getConnections());
        dto.setPronouns(
            user.getPronouns() == null || user.getPronouns().isBlank() ? List.of() : List.of(user.getPronouns().split(","))
        );
        dto.setLanguagesSpoken(
            user.getLanguagesSpoken() == null ? List.of() : List.of(user.getLanguagesSpoken().split(","))
        );
        dto.setBanned(user.getRoles().contains(Role.ROLE_BANNED));
        dto.setCountry(user.getCountry());

        return dto;
    }

    @Deprecated
    public static UserProfileDto fromScheduleLine(ScheduleLineRunner runner) {
        final var dto = new UserProfileDto();

        final User user = runner.getUser();

        if (user == null) {
            dto.setId(-1);
            dto.setUsername(runner.getRunnerName());
            dto.setDisplayName(runner.getRunnerName());
            dto.setEnabled(true);
            dto.setConnections(List.of());
            dto.setPronouns(List.of());
            dto.setLanguagesSpoken(null);
            dto.setBanned(false);
            dto.setCountry(null);
            return dto;
        }

        return fromUserNoHistory(user);
    }
}
