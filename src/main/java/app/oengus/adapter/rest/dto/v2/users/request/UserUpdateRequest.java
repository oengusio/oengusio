package app.oengus.adapter.rest.dto.v2.users.request;

import app.oengus.adapter.rest.dto.v2.users.ConnectionDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Schema
public class UserUpdateRequest {
    private String username;

    private String displayName;

    private String email;

    private boolean enabled;

    @Schema(description = "The preferred pronouns of this user")
    private List<String> pronouns = new ArrayList<>();

    @Schema(description = "The languages that this user speaks")
    private List<String> languagesSpoken = new ArrayList<>();

    @Nullable
    @Schema(description = "The country that this user resides in")
    private String country;

    // TODO: validation on the model
    @Schema(description = "Connected accounts of this user")
    private List<ConnectionDto> connections;

    private boolean mfaEnabled;

    private String discordId;

    private String twitchId;

    private String patreonId;
}
