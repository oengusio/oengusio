package app.oengus.adapter.rest.dto.v2.marathon;

import app.oengus.domain.submission.RunType;
import app.oengus.entity.dto.OpponentCategoryDto;
import lombok.Getter;
import lombok.Setter;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class CategoryDto {
    private int id;
    private String name;
    private Duration estimate;
    private String description;
    private String video;
    // TODO: include code as well?
    private RunType type;
    private int gameId;
    private int userId;
    private List<OpponentCategoryDto> opponents = new ArrayList<>();
}
