package app.oengus.entity.dto.schedule;

import app.oengus.entity.model.ScheduleLine;
import app.oengus.spring.model.Views;
import com.fasterxml.jackson.annotation.JsonView;

public class ScheduleTickerDto {
    @JsonView(Views.Public.class)
    public ScheduleLine previous = null;

    @JsonView(Views.Public.class)
    public ScheduleLine current = null;

    @JsonView(Views.Public.class)
    public ScheduleLine next = null;

    public ScheduleTickerDto setPrevious(ScheduleLine previous) {
        this.previous = previous;
        return this;
    }

    public ScheduleTickerDto setCurrent(ScheduleLine current) {
        this.current = current;
        return this;
    }

    public ScheduleTickerDto setNext(ScheduleLine next) {
        this.next = next;
        return this;
    }
}
