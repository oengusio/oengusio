package app.oengus.util;

public class StringUtils {
    public static String limit(String input, int limit) {
        if (input.length() > limit) {
            return input.substring(0, limit);
        }

        return input;
    }
}
