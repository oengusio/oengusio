package app.oengus.service.export;

import app.oengus.entity.dto.ScheduleDto;
import app.oengus.entity.dto.ScheduleLineDto;
import javassist.NotFoundException;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.QuoteMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;
import java.util.Locale;

@Component
public class ScheduleCsvExporter implements Exporter {

	@Autowired
	private ScheduleHelper scheduleHelper;

	@Override
	public Writer export(final String marathonId, final String zoneId, final String language) throws IOException, NotFoundException {
        final ScheduleDto schedule = this.scheduleHelper.getSchedule(marathonId, zoneId);

        if (schedule == null) {
            throw new NotFoundException("Schedule not found");
        }

        final List<ScheduleLineDto> scheduleLineDtos = schedule.getLinesWithTime();
		final Locale locale = Locale.forLanguageTag(language);
		final StringWriter out = new StringWriter();
		if (!scheduleLineDtos.isEmpty()) {
			try (final CSVPrinter printer = new CSVPrinter(out,
					CSVFormat.RFC4180.builder()
                        .setHeader(scheduleLineDtos.get(0).getCsvHeaders())
                        .setQuoteMode(QuoteMode.NON_NUMERIC)
                        .build()
            )) {
				for (final ScheduleLineDto scheduleLineDto : scheduleLineDtos) {
					printer.printRecords(scheduleLineDto.getCsvRecords(locale));
				}
			}
		}

		return out;
	}
}
