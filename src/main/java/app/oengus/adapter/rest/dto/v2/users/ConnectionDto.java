package app.oengus.adapter.rest.dto.v2.users;

import app.oengus.domain.SocialPlatform;
import app.oengus.adapter.jpa.entity.SocialAccount;
import io.swagger.v3.oas.annotations.media.Schema;
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

    public static ConnectionDto from(SocialAccount account) {
        final ConnectionDto dto = new ConnectionDto();

        dto.setId(account.getId());
        dto.setPlatform(account.getPlatform());
        dto.setUsername(account.getUsername());

        return dto;
    }

    public SocialAccount toSocialAccount() {
        final SocialAccount account = new SocialAccount();

        account.setId(this.id);
        account.setPlatform(this.platform);
        account.setUsername(this.username);

        return account;
    }
}
