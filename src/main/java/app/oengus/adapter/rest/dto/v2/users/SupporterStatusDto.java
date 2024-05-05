package app.oengus.adapter.rest.dto.v2.users;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Supporter status for a user, determines if a user has access to paid features.")
public class SupporterStatusDto {
    private boolean sponsor;
    private boolean patron;

    public boolean isAnySupporter() {
        return this.sponsor || this.patron;
    }
}
