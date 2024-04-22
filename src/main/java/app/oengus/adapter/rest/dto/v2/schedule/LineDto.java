package app.oengus.adapter.rest.dto.v2.schedule;

import app.oengus.domain.submission.RunType;
import app.oengus.adapter.jpa.entity.ScheduleLine;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Schema
@Getter
@Setter
public class LineDto {
    @Schema(description = "Unique id for this schedule line")
    private int id;

    @Schema(description = "The name of the game")
    private String game;
    @Schema(description = "The console that the game is being ran on")
    private String console;
    @Schema(description = "true if the console is emulated")
    private boolean emulated;
    @Schema(description = "Aspect ratio of the game")
    private String ratio;
    @Schema(description = "The specific run type")
    private RunType type;
    @Schema(description = "People that will participate in this run")
    private List<LineRunnerDto> runners = new ArrayList<>();
    @Schema(description = "Category that will be showcased")
    private String category;
    @Schema(
        description = "Approximate duration of the run. Formatted in the ISO-8601 duration format.",
        example = "PT5H30M20S"
    )
    private Duration estimate;
    @Schema(
        description = "Time allocated to get the run set-up. Formatted in the ISO-8601 duration format.",
        example = "PT5H30M20S"
    )
    private Duration setupTime;

    @Schema(description = "Position of this line in the schedule")
    private int position;
    @Schema(description = "I am not sure what this does tbh, it's just there for v1 compatibility")
    private boolean customRun;
    @Schema(description = "true if this is a setup block. NOTE: enabling this nulls all properties related to the game.")
    private boolean setupBlock;
    @Schema(description = "Text to be displayed on the setup block")
    private String setupBlockText;
    @Schema(description = "Custom data assigned by a marathon administrator. Useful for automation software like nodecg-speedcontrol. Speedcontrol will parse this string as a json object.")
    private String customData;
    @Schema(description = "The date and time of when this run is planned to start")
    private ZonedDateTime date;

    @Deprecated(forRemoval = true)
    public static LineDto fromLine(ScheduleLine line, boolean withCustomData) {
        final LineDto dto = new LineDto();

        dto.setId(line.getId());

        if (withCustomData) {
            dto.setCustomData(line.getCustomData());
        }

        if (line.isSetupBlock()) {
            dto.setSetupBlock(true);
            // TODO: not sure what to use
            dto.setEstimate(line.getEstimate());
            dto.setSetupTime(line.getSetupTime());
            dto.setSetupBlockText(line.getSetupBlockText());

            return dto;
        }

        dto.setSetupBlock(false);
        dto.setGame(line.getGameName());
        dto.setConsole(line.getConsole());
        dto.setEmulated(line.isEmulated());
        dto.setRatio(line.getRatio());
        dto.setType(line.getType());
        dto.setRunners(line.getRunners().stream().map(LineRunnerDto::fromLineRunner).toList());
        dto.setCategory(line.getCategoryName());
        dto.setEstimate(line.getEstimate());
        dto.setSetupTime(line.getSetupTime());
        dto.setPosition(line.getPosition());
        dto.setCustomRun(line.isCustomRun());

        return dto;
    }
}
