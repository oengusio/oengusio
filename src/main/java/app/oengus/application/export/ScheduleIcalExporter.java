package app.oengus.application.export;

import app.oengus.application.helper.ScheduleHelper;
import app.oengus.application.port.persistence.MarathonPersistencePort;
import app.oengus.application.port.persistence.SchedulePersistencePort;
import app.oengus.domain.exception.MarathonNotFoundException;
import app.oengus.domain.exception.schedule.ScheduleNotFoundException;
import app.oengus.domain.schedule.Line;
import app.oengus.domain.schedule.Runner;
import app.oengus.domain.schedule.Schedule;
import lombok.RequiredArgsConstructor;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.TimeZone;
import net.fortuna.ical4j.model.TimeZoneRegistry;
import net.fortuna.ical4j.model.TimeZoneRegistryFactory;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.util.RandomUidGenerator;
import net.fortuna.ical4j.util.UidGenerator;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ScheduleIcalExporter implements Exporter {
    private final MarathonPersistencePort marathonPersistencePort;
    private final SchedulePersistencePort schedulePersistencePort;

    private final TimeZoneRegistry registry = TimeZoneRegistryFactory.getInstance().createRegistry();
    private final UidGenerator ug = new RandomUidGenerator();

    @Override
    public Writer export(final String marathonId, final int itemId, final String zoneId, final String language) throws IOException {
        final Schedule schedule = this.schedulePersistencePort.findByIdForMarathon(marathonId, itemId).orElseThrow(
            ScheduleNotFoundException::new
        );
        final var marathon = this.marathonPersistencePort.findById(marathonId).orElseThrow(
            MarathonNotFoundException::new
        );

        final var resourceBundle = ResourceBundle.getBundle("export.Exports", Locale.forLanguageTag(language));
        final Calendar calendar = new Calendar();

        calendar.getProperties().add(new ProdId(marathon.getName()));
        calendar.getProperties().add(new CalScale(CalScale.VALUE_GREGORIAN));

        final TimeZone timeZone = this.registry.getTimeZone(zoneId);
        final var rawLines = schedule.getLines();
        final var timedLines = ScheduleHelper.mapTimeToZone(rawLines, zoneId);

        timedLines.forEach(
            (line) -> calendar.getComponents().add(
                this.mapLineToEvent(line, timeZone, resourceBundle)
            )
        );

        final StringWriter out = new StringWriter();
        out.append(calendar.toString());
        return out;
    }

    private VEvent mapLineToEvent(final Line line, final TimeZone tz, final ResourceBundle resourceBundle) {
        final var messageFormat = new MessageFormat(
            resourceBundle.getString("schedule.export.ics.title.pattern")
        );
        final String[] arguments = {
            line.getGameName(),
            line.getCategoryName(),
            line.getRunners()
                .stream()
                .map(Runner::getEffectiveDisplay)
                .collect(Collectors.joining(", "))
        };
        final String title = messageFormat.format(arguments);
        final VEvent event = new VEvent(
            line.getDate(),
            line.getDate().plus(line.getEstimate()),
            title
        );

        tz.getVTimeZone().getTimeZoneId().ifPresent(event.getProperties()::add);
        event.getProperties().add(this.ug.generateUid());

        return event;
    }
}
