package app.oengus.adapter.rest.dto.v2.users;

import app.oengus.adapter.rest.Views;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.ZonedDateTime;

@Schema
public class ModeratedHistoryDto {
    @JsonView(Views.Public.class)
    @Schema(description = "The id of the marathon this run was submitted to")
    private String marathonId;

    @JsonView(Views.Public.class)
    @Schema(description = "The name of the marathon this run was submitted to")
    private String marathonName;

    @JsonView(Views.Public.class)
    @Schema(description = "The start date of the marathon this run was submitted to")
    private ZonedDateTime marathonStartDate;

    public String getMarathonId() {
        return marathonId;
    }

    public void setMarathonId(String marathonId) {
        this.marathonId = marathonId;
    }

    public String getMarathonName() {
        return marathonName;
    }

    public void setMarathonName(String marathonName) {
        this.marathonName = marathonName;
    }

    public ZonedDateTime getMarathonStartDate() {
        return marathonStartDate;
    }

    public void setMarathonStartDate(ZonedDateTime marathonStartDate) {
        this.marathonStartDate = marathonStartDate;
    }
}
