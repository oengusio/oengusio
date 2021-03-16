package app.oengus.entity.dto.horaro;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "schedule"
})
public class Horaro {

    /**
     * (Required)
     */
    @JsonProperty("schedule")
    private HoraroSchedule schedule;

    /**
     * (Required)
     */
    @JsonProperty("schedule")
    public HoraroSchedule getSchedule() {
        return this.schedule;
    }

    /**
     * (Required)
     */
    @JsonProperty("schedule")
    public void setSchedule(final HoraroSchedule schedule) {
        this.schedule = schedule;
    }

}
