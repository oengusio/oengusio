package app.oengus.adapter.rest.dto.v2.marathon.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema
public class ModeratorsUpdateRequest {
    @Schema(
        description = "User IDs to used as moderators. Existing moderators not present in this array will be removed from the moderator list.",
        required = true
    )
    private int[] userIds;
}
