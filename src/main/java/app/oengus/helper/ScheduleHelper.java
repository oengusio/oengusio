package app.oengus.helper;

import app.oengus.domain.schedule.Line;

import java.time.ZoneId;
import java.util.List;

public class ScheduleHelper {
    public static List<Line> mapTimeToZone(List<Line> lines, String zoneId) {
        final var zone = ZoneId.of(zoneId);

        // TODO: can be a foreach
        for (final Line line : lines) {
            line.setDate(
                line.getDate()
                    .withSecond(0)
                    .withZoneSameInstant(zone)
            );
        }

        return lines;
    }
}
