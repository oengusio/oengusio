package app.oengus.adapter.rest.dto.v2.schedule.request;

import app.oengus.adapter.rest.dto.v2.schedule.LineDto;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
public class LineUpdateRequestDto {
    @NotNull
//    @Size(max = 50) // TODO: do we want to give a max?
    private List<LineDto> data;
}
