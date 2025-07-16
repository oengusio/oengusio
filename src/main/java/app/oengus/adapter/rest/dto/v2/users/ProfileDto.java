package app.oengus.adapter.rest.dto.v2.users;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Schema
public class ProfileDto {
    @Schema(description = "The unique id of this user")
    private int id;

    @Schema(description = "The unique username of this user")
    private String username;

    @Schema(description = "The name that will be shown for the user in the interface")
    private String displayName;

    @Schema(description = "True if this profile is enabled, false otherwise")
    private boolean enabled;

    @Schema(description = "The preferred pronouns of this user")
    private List<String> pronouns = new ArrayList<>();

    @Schema(description = "The languages that this user speaks")
    private List<String> languagesSpoken = new ArrayList<>();

    @Schema(description = "True if this user is banned on the oengus platform")
    private boolean banned;

    @Nullable
    @Schema(description = "The country that this user resides in")
    private String country;

    @Schema(description = "True if the user wishes to display their saved games publicly. Calling the saved games API will return in empty responses when this is false.")
    private boolean savedGamesPublic;

    @Schema(description = "Connected accounts of this user")
    private List<ConnectionDto> connections;
}
