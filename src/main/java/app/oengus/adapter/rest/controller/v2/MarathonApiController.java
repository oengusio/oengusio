package app.oengus.adapter.rest.controller.v2;

import app.oengus.adapter.rest.dto.v2.MarathonHomeDto;
import app.oengus.adapter.rest.dto.v2.marathon.MarathonDto;
import app.oengus.service.MarathonService;
import app.oengus.service.OengusWebhookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import java.security.Principal;

import static app.oengus.helper.HeaderHelpers.cachingHeaders;

/**
 * TODO: api ideas
 *  GET /v2/marathons/{ID}/moderators
 *  PUT /v2/marathons/{ID}/moderators Body: {"user_ids": [int...]}
 *  DELETE /v2/marathons/{ID}/moderators/{userID}
 *
 *  Separate routes for questions as well
 */
@RestController("v2MarathonController")
public class MarathonApiController implements MarathonApi {

    private final MarathonService marathonService;
    private final OengusWebhookService webhookService;

    public MarathonApiController(
        final MarathonService marathonService, final OengusWebhookService webhookService
    ) {
        this.marathonService = marathonService;
        this.webhookService = webhookService;
    }

    @Override
    public ResponseEntity<MarathonHomeDto> getMarathonsForHome() {
        return ResponseEntity.ok()
            .headers(cachingHeaders(5, false))
            .body(
                this.marathonService.findMarathonsForHome()
            );
    }

    @PostMapping
    @RolesAllowed({"ROLE_USER"})
    @PreAuthorize("isAuthenticated() && !isBanned()")
    @Operation(
        summary = "Create a marathon",
        responses = {
            @ApiResponse(description = "Marathon created", responseCode = "201", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MarathonDto.class)))
            // TODO: bindingResult error construction
        }
    )
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
