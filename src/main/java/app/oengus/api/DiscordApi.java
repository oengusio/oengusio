package app.oengus.api;

import app.oengus.entity.model.api.discord.DiscordGuild;
import app.oengus.entity.model.api.discord.DiscordInvite;
import app.oengus.entity.model.api.discord.DiscordMember;
import app.oengus.entity.model.api.discord.DiscordUser;
import app.oengus.spring.CoreFeignConfiguration;
import app.oengus.spring.model.AccessToken;
import feign.Headers;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE;

@FeignClient(value = "discord", url = "https://discordapp.com/api/v8", configuration = CoreFeignConfiguration.class)
public interface DiscordApi {

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @RequestMapping(method = RequestMethod.POST, value = "/oauth2/token", consumes = APPLICATION_FORM_URLENCODED_VALUE)
    AccessToken getAccessToken(@RequestBody Map<String, ?> body);

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
