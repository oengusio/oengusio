package app.oengus.web.v2;

import app.oengus.entity.dto.v2.marathon.MarathonDto;
import app.oengus.service.MarathonService;
import app.oengus.service.OengusWebhookService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import java.security.Principal;

/**
 * TODO: api ideas
 *  GET /v2/marathons/{ID}/moderators
 *  PUT /v2/marathons/{ID}/moderators Body: {"user_ids": [int...]}
 *  DELETE /v2/marathons/{ID}/moderators/{userID}
 *
 *  Separate routes for questions as well
 */
@Api
@RestController("v2MarathonController")
@RequestMapping("/v2/marathons")
public class MarathonController {

    private final MarathonService marathonService;
    private final OengusWebhookService webhookService;

    public MarathonController(
        final MarathonService marathonService, final OengusWebhookService webhookService
    ) {
        this.marathonService = marathonService;
        this.webhookService = webhookService;
    }

    @PostMapping
    @RolesAllowed({"ROLE_USER"})
    @PreAuthorize("isAuthenticated() && !isBanned()")
//    @ApiIgnore
    @ApiOperation("Create a marathon")
    public ResponseEntity<?> create(
        @RequestBody @Valid final MarathonDto request,
        final Principal principal,
        final BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }

        /*final Marathon created = this.marathonService.create(marathon,
            PrincipalHelper.getUserFromPrincipal(principal));
        if (created != null) {
            return ResponseEntity.created(URI.create(created.getId())).build();
        } else {
            return ResponseEntity.noContent().build();
        }*/
        return null;
    }
}
