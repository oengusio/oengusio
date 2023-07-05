package app.oengus.entity.dto;

import app.oengus.spring.model.Views;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@ApiResponse
public class ScheduleTickerDto {
    @JsonView(Views.Public.class)
    public ScheduleLineDto previous = null;

    @JsonView(Views.Public.class)
    public ScheduleLineDto current = null;

    @JsonView(Views.Public.class)
    public ScheduleLineDto next = null;

    public ScheduleTickerDto setPrevious(ScheduleLineDto previous) {
        this.previous = previous;
        return this;
    }

    public ScheduleTickerDto setCurrent(ScheduleLineDto current) {
        this.current = current;
        return this;
    }

    public ScheduleTickerDto setNext(ScheduleLineDto next) {
        this.next = next;
        return this;
    }
}
