package app.oengus.helper;

import app.oengus.entity.dto.UserDto;
import app.oengus.entity.dto.UserProfileDto;
import app.oengus.entity.model.User;

public class StringHelper {
    public static String escapeMarkdown(String input) {
        return input.replace("*", "\\*")
            .replace("_", "\\_")
            .replace("`", "\\`")
            .replace(">", "\\>")
            .replace("||", "\\||")
            ;
    }

    public static String getUserDisplay(User user) {
        return "%s (%s)".formatted(user.getDisplayName(), user.getUsername());
    }

    public static String getUserDisplay(UserProfileDto user) {
        return "%s (%s)".formatted(user.getDisplayName(), user.getUsername());
    }
}
