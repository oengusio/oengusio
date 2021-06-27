package app.oengus.helper;

public class StringHelper {
    public static String escapeMarkdown(String input) {
        return input.replace("*", "\\*")
            .replace("_", "\\_")
            .replace("`", "\\`")
            .replace(">", "\\>")
            .replace("||", "\\||")
            ;
    }
}
