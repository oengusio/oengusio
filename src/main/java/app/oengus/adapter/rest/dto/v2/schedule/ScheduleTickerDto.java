package app.oengus.adapter.rest.dto.v2.schedule;

import app.oengus.adapter.rest.Views;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.Getter;
import lombok.Setter;

// TODO: model documentation
@Getter
@Setter
@ApiResponse
public class ScheduleTickerDto {
    @JsonView(Views.Public.class)
    private LineDto previous = null;

    @JsonView(Views.Public.class)
    private LineDto current = null;

    @JsonView(Views.Public.class)
    private LineDto next = null;
}
