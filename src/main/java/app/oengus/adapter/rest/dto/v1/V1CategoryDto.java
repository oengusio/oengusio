package app.oengus.adapter.rest.dto.v1;

import app.oengus.domain.submission.RunType;

import java.time.Duration;
import java.util.List;

public class V1CategoryDto {
    private int id;
    private int gameId;
    private String name;
    private Duration estimate;
    private String description;
    private String video;
    private RunType type;
    private String code;
    private List<V1OpponentDto> opponents;
}
