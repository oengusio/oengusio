package app.oengus.helper;

import app.oengus.entity.IUsername;

public class StringHelper {
    public static String escapeMarkdown(String input) {
        return input.replace("*", "\\*")
            .replace("_", "\\_")
            .replace("`", "\\`")
            .replace(">", "\\>")
            .replace("||", "\\||")
            ;
    }

    public static String getUserDisplay(IUsername user) {
        return "%s (%s)".formatted(user.getDisplayName(), user.getUsername());
    }
}
