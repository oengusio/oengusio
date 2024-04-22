package app.oengus.application.export;

import org.apache.commons.lang3.time.DurationFormatUtils;

import java.time.Duration;

public class TimeHelpers {
    public static String formatDuration(final Duration duration) {
        return DurationFormatUtils.formatDuration(duration.toMillis(), "H:mm:ss", true);
    }
}
