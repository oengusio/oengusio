package app.oengus.adapter.rest.dto.v2.users;

import app.oengus.domain.Connection;
import app.oengus.domain.SocialPlatform;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema
public class ConnectionDto {
    @Schema(description = "Database id of this connection")
    private int id;

    @Schema(description = "The platform that this connection links to")
    private SocialPlatform platform;

    @Schema(description = "The username for the platform")
    private String username;

    @JsonIgnore
    @AssertTrue(message = "The username does not have a valid format for the platform")
    public boolean isUsernameValidForPlatform() {
        return Connection.isUsernameValidForPlatform(this.username, this.platform);
    }
}
