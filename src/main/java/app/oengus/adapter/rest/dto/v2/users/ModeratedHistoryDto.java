package app.oengus.adapter.rest.dto.v2.users;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

@Getter
@Setter
@Schema
public class ModeratedHistoryDto {
    @Schema(description = "The id of the marathon this run was submitted to")
    private String marathonId;

    @Schema(description = "The name of the marathon this run was submitted to")
    private String marathonName;

    @Schema(description = "The start date of the marathon this run was submitted to")
    private ZonedDateTime marathonStartDate;
}
