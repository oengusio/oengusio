package app.oengus.adapter.rest.dto.v2.schedule;

import app.oengus.adapter.rest.dto.v2.users.ProfileDto;
import app.oengus.adapter.jpa.entity.ScheduleLineRunner;
import app.oengus.adapter.jpa.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nullable;

@Getter
@Setter
@Schema
public class LineRunnerDto {
    @Nullable
    @Schema(description = "The Oengus profile of the runner. This profile is nullable if the runner is not an Oengus user.")
    private ProfileDto profile;

    @Nullable
    @Schema(description = "The name of runner that displays in the schedule. Reflects the display name from the profile if present.")
    private String runnerName;

    public String getRunnerName() {
        if (this.profile == null) {
            return this.runnerName;
        }

        return this.profile.getDisplayName();
    }

    @Deprecated(forRemoval = true)
    public static LineRunnerDto fromLineRunner(ScheduleLineRunner lineRunner) {
        final LineRunnerDto dto = new LineRunnerDto();
        final User user = lineRunner.getUser();

        if (user == null) {
            dto.setProfile(null);
        } else {
            dto.setProfile(ProfileDto.fromUser(user));
        }

        dto.setRunnerName(lineRunner.getRunnerName());

        return dto;
    }
}
