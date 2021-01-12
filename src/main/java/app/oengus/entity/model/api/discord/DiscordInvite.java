package app.oengus.entity.model.api.discord;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DiscordInvite {
    private DiscordGuild guild;

    public DiscordGuild getGuild() {
        return guild;
    }

    public void setGuild(DiscordGuild guild) {
        this.guild = guild;
    }
}
