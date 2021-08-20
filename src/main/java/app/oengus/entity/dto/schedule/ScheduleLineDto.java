package app.oengus.entity.dto.schedule;

import app.oengus.entity.model.RunType;
import app.oengus.entity.model.ScheduleLine;
import app.oengus.helper.TimeHelpers;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

@ApiModel(description = "A line in a schedule, contains all sorts of info about a run")
public class ScheduleLineDto {

    private static final List<String> DEFAULT_HEADERS =
        List.of("time", "runners", "game", "category", "type", "console", "estimate", "setup_time", "custom_data");

    @ApiModelProperty(value = "The id of this schedule line")
    private int id;
    @ApiModelProperty(value = "The name of the game")
    private String gameName;
    @ApiModelProperty(value = "The console that the game is running on")
    private String console;
    @ApiModelProperty(value = "True if the console is emulated")
    private boolean emulated;
    @ApiModelProperty(value = "The display ratio of the game")
    private String ratio;
    @ApiModelProperty(value = "The name of the category that is being ran")
    private String categoryName;
    @ApiModelProperty(value = "The estimate of the run")
    private Duration estimate;
    @ApiModelProperty(value = "The setup time assigned for this run")
    private Duration setupTime;
    @ApiModelProperty(value = "True if this line is a setup block")
    private boolean setupBlock;
    @ApiModelProperty(value = "True if this is a custom run (idk what that means)")
    private boolean customRun;
    @ApiModelProperty(value = "The position of this run in the schedule")
    private int position;
    @ApiModelProperty(value = "The type of run")
    private RunType type;
    private List<RunnerDto> runners;
    private String setupBlockText;

    private ZonedDateTime date; // ??

    private String customData;

    private ZonedDateTime time; // TODO: remove? can be set on the date

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public String getConsole() {
        return console;
    }

    public void setConsole(String console) {
        this.console = console;
    }

    public boolean isEmulated() {
        return emulated;
    }

    public void setEmulated(boolean emulated) {
        this.emulated = emulated;
    }

    public String getRatio() {
        return ratio;
    }

    public void setRatio(String ratio) {
        this.ratio = ratio;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Duration getEstimate() {
        return estimate;
    }

    public void setEstimate(Duration estimate) {
        this.estimate = estimate;
    }

    public Duration getSetupTime() {
        return setupTime;
    }

    public void setSetupTime(Duration setupTime) {
        this.setupTime = setupTime;
    }

    public boolean isSetupBlock() {
        return setupBlock;
    }

    public void setSetupBlock(boolean setupBlock) {
        this.setupBlock = setupBlock;
    }

    public boolean isCustomRun() {
        return customRun;
    }

    public void setCustomRun(boolean customRun) {
        this.customRun = customRun;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public RunType getType() {
        return type;
    }

    public void setType(RunType type) {
        this.type = type;
    }

    public List<RunnerDto> getRunners() {
        return runners;
    }

    public void setRunners(List<RunnerDto> runners) {
        this.runners = runners;
    }

    public String getSetupBlockText() {
        return setupBlockText;
    }

    public void setSetupBlockText(String setupBlockText) {
        this.setupBlockText = setupBlockText;
    }

    public ZonedDateTime getDate() {
        return date;
    }

    public void setDate(ZonedDateTime date) {
        this.date = date;
    }

    public String getCustomData() {
        return customData;
    }

    public void setCustomData(String customData) {
        this.customData = customData;
    }

    public ZonedDateTime getTime() {
        return this.time;
    }

    public void setTime(final ZonedDateTime time) {
        this.time = time;
    }

    public Duration getEffectiveSetupTime() {
        // set the setup time to 0 for setup blocks
        if (this.isSetupBlock()) {
            return Duration.ZERO;
        }

        return this.getSetupTime();
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
            .map(user -> user.getUsername(locale.toLanguageTag()))
            .collect(Collectors.joining(", ")));
        record.add(this.getGameName());
        record.add(this.getCategoryName());
        record.add(resourceBundle.getString("run.type." + this.getType().name()));
        record.add(this.getConsole());
        record.add(TimeHelpers.formatDuration(this.getEstimate()));
        record.add(TimeHelpers.formatDuration(this.getSetupTime()));
        record.add(this.getCustomData());

        return List.of(record);
    }

    public static ScheduleLineDto fromModel(final ScheduleLine model, final boolean customData) {
        final ScheduleLineDto dto = new ScheduleLineDto();

        dto.setId(model.getId());
        dto.setGameName(model.getGameName());
        dto.setConsole(model.getConsole());
        dto.setEmulated(model.isEmulated());
        dto.setRatio(model.getRatio());
        dto.setCategoryName(model.getCategoryName());
        dto.setEstimate(model.getEstimate());
        dto.setSetupTime(model.getSetupTime());
        dto.setSetupBlock(model.isSetupBlock());
        dto.setCustomRun(model.isCustomRun());
        dto.setPosition(model.getPosition());
        dto.setType(model.getType());
        dto.setRunners(
            model.getRunners().stream().map(RunnerDto::fromModel).collect(Collectors.toList())
        );
        dto.setSetupBlockText(model.getSetupBlockText());
        dto.setDate(model.getDate());
        // TODO: date and time

        if (customData) {
            dto.setCustomData(model.getCustomData());
        }

        return dto;
    }
}
