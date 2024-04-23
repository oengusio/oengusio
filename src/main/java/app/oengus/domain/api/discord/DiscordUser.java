package app.oengus.domain.api.discord;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class DiscordUser {

    private String username;
    private String discriminator;
    private String id;

    public String getAsTag() {
        if (this.discriminator == null || "0".equals(this.discriminator)) {
            return this.getUsername();
        }

        return this.getUsername() + '#' + this.getDiscriminator();
    }
}
