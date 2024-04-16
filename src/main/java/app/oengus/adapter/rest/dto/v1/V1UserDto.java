package app.oengus.adapter.rest.dto.v1;

import app.oengus.entity.model.SocialAccount;
import app.oengus.spring.model.Role;

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
    List<String> languagesSpoken
) {
    public String getUsernameJapanese() {
        return this.displayName;
    }
}
