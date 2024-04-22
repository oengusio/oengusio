package app.oengus.adapter.rest.dto.v1;

import app.oengus.adapter.rest.dto.OpponentCategoryDto;
import app.oengus.domain.submission.RunType;
import lombok.Getter;
import lombok.Setter;

import java.time.Duration;
import java.util.List;

@Getter
@Setter
public class V1CategoryDto {
    private int id;
    private int gameId;
    private String name;
    private Duration estimate;
    private String description;
    private String video;
    private RunType type;
    private String code;
    private List<OpponentCategoryDto> opponents;
}
