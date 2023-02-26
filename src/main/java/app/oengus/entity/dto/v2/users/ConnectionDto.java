package app.oengus.entity.dto.v2.users;

import app.oengus.entity.constants.SocialPlatform;
import app.oengus.entity.model.SocialAccount;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public class ConnectionDto {
    @Schema(description = "Database id of this connection")
    private int id;

    @Schema(description = "The platform that this connection links to")
    private SocialPlatform platform;

    @Schema(description = "The username for the platform")
    private String username;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public SocialPlatform getPlatform() {
        return platform;
    }

    public void setPlatform(SocialPlatform platform) {
        this.platform = platform;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public static ConnectionDto from(SocialAccount account) {
        final ConnectionDto dto = new ConnectionDto();

        dto.setId(account.getId());
        dto.setPlatform(account.getPlatform());
        dto.setUsername(account.getUsername());

        return dto;
    }
}
