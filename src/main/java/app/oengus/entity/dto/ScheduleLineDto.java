package app.oengus.entity.dto;

import app.oengus.entity.model.ScheduleLine;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.time.DurationFormatUtils;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ScheduleLineDto extends ScheduleLine {

	private ZonedDateTime time;

	public ZonedDateTime getTime() {
		return this.time;
	}

	public void setTime(final ZonedDateTime time) {
		this.time = time;
	}

	private static final List<String> DEFAULT_HEADERS =
			List.of("time", "runners", "game", "category", "type", "console", "estimate", "setup_time");

	@JsonIgnore
	public String[] getCsvHeaders() {
		final List<String> headers = new ArrayList<>(DEFAULT_HEADERS);
		String[] array = new String[headers.size()];
		array = headers.toArray(array);
		return array;
	}

	@JsonIgnore
	public List<List<String>> getCsvRecords(final Locale locale) {
		final ResourceBundle resourceBundle = ResourceBundle.getBundle("export.Exports", locale);
		final List<String> record = new ArrayList<>();
		record.add(DateTimeFormatter.ISO_ZONED_DATE_TIME.format(this.time));
		record.add(this.getRunners()
		               .stream()
		               .map(user -> user.getUsername(locale.toLanguageTag()))
		               .collect(Collectors.joining(", ")));
		record.add(this.getGameName());
		record.add(this.getCategoryName());
		record.add(resourceBundle.getString("run.type." + this.getType().name()));
		record.add(this.getConsole());
		record.add(DurationFormatUtils.formatDuration(this.getEstimate().toMillis(), "H:mm:ss", true));
		record.add(DurationFormatUtils.formatDuration(this.getSetupTime().toMillis(), "H:mm:ss", true));
		return List.of(record);
	}
}
