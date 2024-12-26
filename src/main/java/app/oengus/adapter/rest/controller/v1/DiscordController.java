package app.oengus.adapter.rest.controller.v1;

import app.oengus.application.DiscordService;
import app.oengus.application.MarathonService;
import app.oengus.domain.api.discord.DiscordInvite;
import app.oengus.domain.api.discord.DiscordMember;
import app.oengus.domain.exception.OengusBusinessException;
import feign.FeignException;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.tags.Tag;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static app.oengus.adapter.rest.helper.HeaderHelpers.cachingHeaders;

@Hidden
@Tag(name = "discord-v1")
@RestController
@RequiredArgsConstructor
@CrossOrigin(maxAge = 3600)
@RequestMapping("/v1/marathons/{marathonId}/discord")
public class DiscordController {
    private final DiscordService discordService;
    private final MarathonService marathonService;

    @GetMapping("/lookup-invite")
    @PreAuthorize("canUpdateMarathon(#marathonId)")
    public ResponseEntity<?> lookupInvite(@PathVariable("marathonId") final String marathonId,
                                          @RequestParam("invite_code") final String inviteCode) {
        try {
            final DiscordInvite invite = this.discordService.fetchInvite(inviteCode);

            return ResponseEntity.ok()
                .headers(cachingHeaders(10, true))
                .body(invite.getGuild());
        } catch (FeignException e) {
            if (e.status() == 404) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/in-guild/{userId}")
    @PreAuthorize("isAuthenticated() && !isBanned()")
    public ResponseEntity<?> isUserInGuild(@PathVariable("marathonId") final String marathonId,
                                           @PathVariable("userId") final String userId) throws NotFoundException {
        try {
            final var marathon = this.marathonService.findById(marathonId).orElseThrow(
                () -> new NotFoundException("Marathon not found")
            );
            final String guildId = marathon.getDiscordGuildId();

            if (StringUtils.isEmpty(guildId)) {
                throw new OengusBusinessException("NO_GUILD_ID_SET");
            }

            final DiscordMember memberById = this.discordService.getMemberById(guildId, userId);

            // pending members have not yet accepted the rules
            // and are not in the guild technically
            if (memberById.isPending()) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok()
                .headers(cachingHeaders(10, true))
                .build();
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
