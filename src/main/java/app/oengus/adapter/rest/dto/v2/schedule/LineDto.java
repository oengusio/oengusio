package app.oengus.adapter.rest.dto.v2.schedule;

import app.oengus.domain.submission.RunType;
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

    @Schema(description = "Internal ID of a category that was submitted. Only ever used to hide categories from the UI if they are moved to the schedule.")
    private int categoryId;
}
