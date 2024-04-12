package app.oengus.entity.model.api.discord;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class DiscordGuild {
    private String id;
    private String name;
}
