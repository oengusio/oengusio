package app.oengus.entity.dto.schedule;

import app.oengus.entity.dto.user.UserProfileDto;
import app.oengus.entity.model.User;

public class RunnerDto {
    // will contain info about the username and other user info if this is an oengus user
    private UserProfileDto userData = null; // not null if found
    private String username;

    public int getUserId() {
        if (this.userData == null) {
            return -1;
        }

        return this.userData.getId();
    }

    public UserProfileDto getUserData() {
        return userData;
    }

    public void setUserData(UserProfileDto userData) {
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
