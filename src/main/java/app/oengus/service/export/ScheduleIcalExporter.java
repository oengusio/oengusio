package app.oengus.service.export;

import app.oengus.application.port.persistence.MarathonPersistencePort;
import app.oengus.entity.dto.ScheduleLineDto;
import app.oengus.entity.dto.V1ScheduleDto;
import app.oengus.helper.StringHelper;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import net.fortuna.ical4j.model.*;
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

	private final ScheduleHelper scheduleHelper;
    private final MarathonPersistencePort marathonPersistencePort;

	private final TimeZoneRegistry registry = TimeZoneRegistryFactory.getInstance().createRegistry();
	private final UidGenerator ug = new RandomUidGenerator();

    @Override
	public Writer export(final String marathonId, final String zoneId, final String language) throws IOException, NotFoundException {
		final V1ScheduleDto scheduleDto = this.scheduleHelper.getSchedule(marathonId, zoneId);

        if (scheduleDto == null) {
            throw new NotFoundException("Schedule not found");
        }

        final var marathon = this.marathonPersistencePort.findById(marathonId).orElseThrow(
            () -> new NotFoundException("Marathon not found")
        );
		final ResourceBundle resourceBundle =
				ResourceBundle.getBundle("export.Exports", Locale.forLanguageTag(language));

		final Calendar calendar = new Calendar();
		calendar.getProperties().add(new ProdId(marathon.getName()));
		calendar.getProperties().add(CalScale.GREGORIAN);
		final TimeZone timeZone = this.registry.getTimeZone(zoneId);
		scheduleDto.getLinesWithTime()
		           .forEach(scheduleLineDto -> calendar.getComponents()
		                                               .add(this.mapLineToEvent(scheduleLineDto, timeZone,
				                                               resourceBundle)));

		final StringWriter out = new StringWriter();
		out.append(calendar.toString());
		return out;
	}

	private VEvent mapLineToEvent(final ScheduleLineDto scheduleLineDto, final TimeZone tz,
	                              final ResourceBundle resourceBundle) {
		final MessageFormat messageFormat =
				new MessageFormat(resourceBundle.getString("schedule.export.ics.title.pattern"));
		final String[] arguments = {scheduleLineDto.getGameName(),
		                            scheduleLineDto.getCategoryName(),
		                            scheduleLineDto.getRunners()
		                                           .stream()
		                                           .map(StringHelper::getUserDisplay)
				                            .collect(Collectors.joining(", "))};
		final String title = messageFormat.format(arguments);
		final VEvent event = new VEvent(new DateTime(java.util.Date.from(scheduleLineDto.getTime().toInstant()), tz),
				new DateTime(
						java.util.Date.from(scheduleLineDto.getTime().plus(scheduleLineDto.getEstimate()).toInstant()),
						tz),
				title);
		event.getProperties().add(tz.getVTimeZone().getTimeZoneId());
		event.getProperties().add(this.ug.generateUid());
		return event;
	}
}
