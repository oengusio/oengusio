package app.oengus.domain.api.discord;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class DiscordMember {
    private DiscordUser user;
    private boolean pending;
}
