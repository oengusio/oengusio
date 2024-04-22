package app.oengus.domain.schedule;

import app.oengus.domain.submission.RunType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
public class Line {
    private final int id;
    private final int scheduleId;

    // Normal settings
    private String gameName;
    private String console;
    private boolean emulated = false;
    private String ratio;
    private String categoryName;
    private Duration estimate;
    private Duration setupTime;
    private boolean customRun;
    private int position;
    private RunType type;
    private List<Runner> runners = new ArrayList<>();

    // setup block settings
    private boolean setupBlock;
    private String setupBlockText;

    private ZonedDateTime date; // TODO: only put this on the DTO, or here as well?
    private String customData;
}
