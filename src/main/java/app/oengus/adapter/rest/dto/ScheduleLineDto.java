package app.oengus.adapter.rest.dto;

import app.oengus.domain.submission.RunType;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nullable;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.List;

@Getter
@Setter
public class ScheduleLineDto {
    private int id;
    private String gameName;
    private String console;
    private boolean emulated;
    private String ratio;
    private String categoryName;
    // Sigh, I hate that this is stored
    // But it is because of filtering in the UI :/
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
}
