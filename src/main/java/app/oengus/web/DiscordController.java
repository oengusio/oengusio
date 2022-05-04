package app.oengus.web;

import app.oengus.entity.model.Marathon;
import app.oengus.entity.model.api.discord.DiscordInvite;
import app.oengus.entity.model.api.discord.DiscordMember;
import app.oengus.exception.OengusBusinessException;
import app.oengus.service.DiscordApiService;
import app.oengus.service.MarathonService;
import feign.FeignException;
import javassist.NotFoundException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.security.RolesAllowed;

@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping({"/v1/marathons/{marathonId}/discord", "/marathons/{marathonId}/discord"})
@ApiIgnore
public class DiscordController {
    @Autowired
    private DiscordApiService discordApiService;

    @Autowired
    private MarathonService marathonService;

    @GetMapping("/lookup-invite")
    @PreAuthorize("!isBanned() && canUpdateMarathon(#marathonId)")
    public ResponseEntity<?> lookupInvite(@PathVariable("marathonId") final String marathonId,
                                          @RequestParam("invite_code") final String inviteCode) {
        try {
            final DiscordInvite invite = this.discordApiService.fetchInvite(inviteCode);

            return ResponseEntity.ok().body(invite.getGuild());
        } catch (FeignException e) {
            if (e.status() == 404) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/in-guild/{userId}")
    @RolesAllowed({"ROLE_USER"})
    @PreAuthorize("!isBanned()")
    public ResponseEntity<?> isUserInGuild(@PathVariable("marathonId") final String marathonId,
                                           @PathVariable("userId") final String userId) throws NotFoundException {
        try {
            final Marathon marathon = this.marathonService.getById(marathonId);
            final String guildId = marathon.getDiscordGuildId();

            if (StringUtils.isEmpty(guildId)) {
                throw new OengusBusinessException("NO_GUILD_ID_SET");
            }

            final DiscordMember memberById = this.discordApiService.getMemberById(guildId, userId);

            // pending members have not yet accepted the rules
            // and are not in the guild technically
            if (memberById.isPending()) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok().build();
        } catch (FeignException e) {
            if (e.status() == 404) { // member not in guild
                return ResponseEntity.notFound().build();
            } else if (e.status() == 403) { // bot not in guild
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
