package app.oengus.adapter.rest.dto.v2.schedule;

import app.oengus.adapter.rest.dto.v2.users.ProfileDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nullable;
import jakarta.validation.constraints.AssertTrue;

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

    @JsonIgnore
    @AssertTrue(message = "Either profile or runnerName must be set")
    public boolean runnerOrNameIsSet() {
        return (this.profile != null && this.profile.getId() > 0) ||
            (this.runnerName != null && !this.runnerName.isBlank());
    }
}
