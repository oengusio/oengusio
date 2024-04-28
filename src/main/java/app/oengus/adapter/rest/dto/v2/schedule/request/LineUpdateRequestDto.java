package app.oengus.adapter.rest.dto.v2.schedule.request;

import app.oengus.adapter.rest.dto.v2.schedule.LineDto;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
public class LineUpdateRequestDto {
    @NotNull
    @Size(max = 50)
    private List<LineDto> data;
}
