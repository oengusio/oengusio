package app.oengus.api;

import app.oengus.entity.model.api.discord.DiscordGuild;
import app.oengus.entity.model.api.discord.DiscordInvite;
import app.oengus.entity.model.api.discord.DiscordMember;
import app.oengus.entity.model.api.discord.DiscordUser;
import app.oengus.configuration.CoreFeignConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(value = "discord", url = "https://discord.com/api/v10", configuration = CoreFeignConfiguration.class)
public interface DiscordApi {

    @RequestMapping(method = RequestMethod.GET, value = "/users/@me")
    DiscordUser getCurrentUser(@RequestHeader("Authorization") String token);

    @RequestMapping(method = RequestMethod.GET, value = "/users/{id}")
    DiscordUser getUser(@RequestHeader("Authorization") String token, @PathVariable("id") String id);

    @RequestMapping(method = RequestMethod.GET, value = "/invites/{inviteCode}")
    DiscordInvite getInvite(@RequestHeader("Authorization") String token, @PathVariable("inviteCode") String inviteCode);

    @RequestMapping(method = RequestMethod.GET, value = "/guilds/{guildId}")
    DiscordGuild getGuild(@RequestHeader("Authorization") String token, @PathVariable("guildId") String guildId);

    @RequestMapping(method = RequestMethod.GET, value = "/guilds/{guildId}/members/{userId}")
    DiscordMember getGuildMember(@RequestHeader("Authorization") String token, @PathVariable("guildId") String guildId, @PathVariable("userId") String userId);
}
