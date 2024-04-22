package app.oengus.entity.dto;

import app.oengus.domain.submission.RunType;
import app.oengus.adapter.jpa.entity.ScheduleLine;
import app.oengus.helper.StringHelper;
import app.oengus.helper.TimeHelpers;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nullable;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

@Getter
@Setter
public class ScheduleLineDto {
    private static final List<String> DEFAULT_HEADERS =
        List.of("time", "runners", "game", "category", "type", "console", "estimate", "setup_time", "custom_data");

    private int id;
    private String gameName;
    private String console;
    private boolean emulated;
    private String ratio;
    private String categoryName;
    // Sigh, I hate that this is stored
    @Nullable
    private Integer categoryId;
    private Duration estimate;
    private Duration setupTime;
    private boolean setupBlock;
    private boolean customRun;
    private int position;
    private RunType type;
    private List<UserProfileDto> runners;
    private String setupBlockText;
    private ZonedDateTime date;
    private String customDataDTO;

    private ZonedDateTime time;

    public Duration getEffectiveSetupTime() {
        // set the setup time to 0 for setup blocks
        if (this.isSetupBlock()) {
            return Duration.ZERO;
        }

        return getSetupTime();
    }

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
            .map(StringHelper::getUserDisplay)
            .collect(Collectors.joining(", ")));
        record.add(this.getGameName());
        record.add(this.getCategoryName());
        record.add(resourceBundle.getString("run.type." + this.getType().name()));
        record.add(this.getConsole());
        record.add(TimeHelpers.formatDuration(this.getEstimate()));
        record.add(TimeHelpers.formatDuration(this.getSetupTime()));
        record.add(this.getCustomDataDTO());

        return List.of(record);
    }

    @Deprecated(forRemoval = true)
    public static ScheduleLineDto fromLine(final ScheduleLine line) {
        final var dto = new ScheduleLineDto();

        dto.setId(line.getId());
        dto.setGameName(line.getGameName());
        dto.setConsole(line.getConsole());
        dto.setEmulated(line.isEmulated());
        dto.setRatio(line.getRatio());
        dto.setCategoryName(line.getCategoryName());
        dto.setCategoryId(line.getCategoryId());
        dto.setEstimate(line.getEstimate());
        dto.setSetupTime(line.getSetupTime());
        dto.setSetupBlock(line.isSetupBlock());
        dto.setCustomRun(line.isCustomRun());
        dto.setPosition(line.getPosition());
        dto.setType(line.getType());
        dto.setRunners(line.getRunners()
            .stream()
            .map(UserProfileDto::fromScheduleLine)
            .toList());
        dto.setSetupBlockText(line.getSetupBlockText());
        dto.setDate(line.getDate());
        dto.setCustomDataDTO(line.getCustomData());

        return dto;
    }
}
