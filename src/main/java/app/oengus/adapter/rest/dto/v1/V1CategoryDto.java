package app.oengus.adapter.rest.dto.v1;

import app.oengus.adapter.rest.dto.OpponentCategoryDto;
import app.oengus.domain.submission.RunType;
import jakarta.validation.constraints.Min;
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
    @Min(value = 0)
    private int expectedRunnerCount;
    private List<OpponentCategoryDto> opponents;

    // This is only for backwards compatibility
    public List<OpponentCategoryDto> getOpponentDtos() {
        return this.opponents;
    }
}
