package app.oengus.adapter.rest.dto.v2.users;

import app.oengus.domain.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Schema
public class SelfUserDto {
    @Schema(description = "The unique id of this user")
    private int id;

    @Schema(description = "The unique username of this user")
    private String username;

    @Schema(description = "The name that will be shown for the user in the interface")
    private String displayName;

    private String email;

    @Schema(description = "True if this profile is enabled, false otherwise")
    private boolean enabled;

    @Schema(description = "The preferred pronouns of this user")
    private List<String> pronouns = new ArrayList<>();

    @Schema(description = "The languages that this user speaks")
    private List<String> languagesSpoken = new ArrayList<>();

    private List<Role> roles = new ArrayList<>();

    @Nullable
    @Schema(description = "The country that this user resides in")
    private String country;

    @Schema(description = "Connected accounts of this user")
    private List<ConnectionDto> connections;

    @Schema(description = "True if you have 2fa/mfa enabled")
    private boolean mfaEnabled;

    @Schema(description = "True if you verified your email")
    private boolean emailVerified;

    private String discordId;
    private String twitchId;
    private String patreonId;
}
