package app.oengus.entity.dto.schedule;

import app.oengus.entity.dto.user.UserProfileDto;
import app.oengus.entity.model.User;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.annotation.Nullable;

@ApiModel(description = "Represents a runner in a schedule")
public class RunnerDto {
    // will contain info about the username and other user info if this is an oengus user
    @Nullable
    @ApiModelProperty(value = "The user profile data, only present for Oengus users")
    private UserProfileDto userData = null; // not null if found
    @ApiModelProperty(value = "The name of this user")
    private String username;

    public int getUserId() {
        if (this.userData == null) {
            return -1;
        }

        return this.userData.getId();
    }

    @Nullable
    public UserProfileDto getUserData() {
        return userData;
    }

    public void setUserData(@Nullable UserProfileDto userData) {
        this.userData = userData;
    }

    public String getUsername(final String locale) {
        if (this.userData != null) {
            return this.userData.getUsername(locale);
        }

        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public static RunnerDto fromModel(final User user) {
        final RunnerDto dto = new RunnerDto();

        dto.setUsername(user.getUsername());
        dto.setUserData(UserProfileDto.fromModel(user));

        return dto;
    }
}
