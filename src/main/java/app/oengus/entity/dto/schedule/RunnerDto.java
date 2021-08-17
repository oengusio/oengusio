package app.oengus.entity.dto.schedule;

import app.oengus.entity.dto.user.UserProfileDto;
import app.oengus.entity.model.User;

public class RunnerDto {
    // will contain info about the username and other user info if this is an oengus user
    private int userId;
    private UserProfileDto userData; // not null if found
    private String username;

    public String getUsername(final String locale) {
        return "TODO";
    }

    public static RunnerDto fromModel(final User user) {
        return new RunnerDto();
    }
}
