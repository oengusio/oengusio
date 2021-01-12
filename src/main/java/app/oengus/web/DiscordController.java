package app.oengus.web;

import app.oengus.entity.model.api.discord.DiscordGuild;
import app.oengus.service.DiscordApiService;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/marathon/{marathonId}/discord")
public class DiscordController {
    @Autowired
    private DiscordApiService discordApiService;

    @GetMapping("/lookup")
    @PreAuthorize("!isBanned() && canUpdateMarathon(#marathonId)")
    public ResponseEntity<?> lookupGuild(@PathVariable("marathonId") final String marathonId,
                                                  @RequestParam("guild_id") final String guildId) {
        try {
            final DiscordGuild guildById = this.discordApiService.getGuildById(guildId);

            return ResponseEntity.ok().body(guildById);
        } catch (FeignException e) {
            if (e.status() == 404) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
