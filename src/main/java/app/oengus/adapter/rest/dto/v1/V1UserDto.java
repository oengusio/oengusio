package app.oengus.adapter.rest.dto.v1;

import app.oengus.adapter.jpa.entity.SocialAccount;
import app.oengus.adapter.rest.Views;
import app.oengus.domain.Role;
import com.fasterxml.jackson.annotation.JsonView;

import java.util.List;

// Yes I know we are using database models here
// I don't care if v1 does it.
public record V1UserDto(
    int id,
    String username,
    String displayName,
    boolean enabled,
    List<Role> roles,
    List<SocialAccount> connections,
    boolean emailVerified,
    List<String> pronouns,
    String country,
    List<String> languagesSpoken,

    // annoying stuff below
    @JsonView(Views.Internal.class)
    boolean mfaEnabled,
    @JsonView(Views.Internal.class)
    String email,
    @JsonView(Views.Internal.class)
    String discordId,
    @JsonView(Views.Internal.class)
    String twitchId,
    @JsonView(Views.Internal.class)
    String patreonId
) {
    public String getUsernameJapanese() {
        return this.displayName;
    }
}
