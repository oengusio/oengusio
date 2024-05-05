package app.oengus.application.export;

import app.oengus.application.port.persistence.SchedulePersistencePort;
import app.oengus.domain.exception.schedule.ScheduleNotFoundException;
import app.oengus.domain.schedule.Line;
import app.oengus.domain.schedule.Runner;
import app.oengus.domain.schedule.Schedule;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.QuoteMode;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ScheduleCsvExporter implements Exporter {
    private static final List<String> DEFAULT_HEADERS =
        List.of("time", "runners", "game", "category", "type", "console", "estimate", "setup_time", "custom_data");

    private final SchedulePersistencePort schedulePersistencePort;

	@Override
	public Writer export(final String marathonId, final int itemId, final String zoneId, final String language) throws IOException {
        final Schedule schedule = this.schedulePersistencePort.findByIdForMarathon(marathonId, itemId).orElseThrow(
            ScheduleNotFoundException::new
        );

        final var lines = schedule.getLines();
		final Locale locale = Locale.forLanguageTag(language);
		final StringWriter out = new StringWriter();

		if (!lines.isEmpty()) {
			try (final CSVPrinter printer = new CSVPrinter(out,
					CSVFormat.RFC4180.builder()
                        .setHeader(this.getCsvHeaders())
                        .setQuoteMode(QuoteMode.NON_NUMERIC)
                        .build()
            )) {
				for (final Line line : lines) {
					printer.printRecords(this.getCsvRecords(line, locale));
				}
			}
		}

		return out;
	}

    public String[] getCsvHeaders() {
        // final List<String> headers = new ArrayList<>(DEFAULT_HEADERS);

        return DEFAULT_HEADERS.toArray(String[]::new);
    }

    @JsonIgnore
    public List<List<String>> getCsvRecords(Line line, final Locale locale) {
        final ResourceBundle resourceBundle = ResourceBundle.getBundle("export.Exports", locale);
        final List<String> record = new ArrayList<>();

        record.add(DateTimeFormatter.ISO_ZONED_DATE_TIME.format(line.getDate()));
        record.add(line.getRunners()
            .stream()
            .map(Runner::getEffectiveDisplay)
            .collect(Collectors.joining(", ")));
        record.add(line.getGameName());
        record.add(line.getCategoryName());
        record.add(resourceBundle.getString("run.type." + line.getType().name()));
        record.add(line.getConsole());
        record.add(TimeHelpers.formatDuration(line.getEstimate()));
        record.add(TimeHelpers.formatDuration(line.getSetupTime()));
        record.add(line.getCustomData());

        return List.of(record);
    }
}
