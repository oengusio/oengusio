package app.oengus.adapter.rest.dto.v2.schedule;

import app.oengus.adapter.rest.dto.v2.users.ProfileDto;
import app.oengus.entity.model.ScheduleLineRunner;
import app.oengus.entity.model.User;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.annotation.Nullable;

@Schema
public class LineRunnerDto {
    @Nullable
    @Schema(description = "The Oengus profile of the runner. This profile is nullable if the runner is not an Oengus user.")
    private ProfileDto profile;

    @Nullable
    @Schema(description = "The name of runner that displays in the schedule. Reflects the display name from the profile if present.")
    private String runnerName;

    @Nullable
    public ProfileDto getProfile() {
        return profile;
    }

    public void setProfile(@Nullable ProfileDto profile) {
        this.profile = profile;
    }

    public String getRunnerName() {
        if (this.profile == null) {
            return this.runnerName;
        }

        return this.profile.getDisplayName();
    }

    public void setRunnerName(@Nullable String runnerName) {
        this.runnerName = runnerName;
    }

    @Deprecated
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
