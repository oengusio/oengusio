package app.oengus.adapter.rest.controller.v1;

import app.oengus.adapter.rest.dto.v1.MarathonBasicInfoDto;
import app.oengus.adapter.rest.dto.v1.request.MarathonCreateRequestDto;
import app.oengus.adapter.rest.dto.v1.request.MarathonUpdateRequestDto;
import app.oengus.adapter.rest.mapper.MarathonDtoMapper;
import app.oengus.application.MarathonService;
import app.oengus.application.port.security.UserSecurityPort;
import app.oengus.domain.marathon.Marathon;
import app.oengus.adapter.rest.dto.MarathonDto;
import app.oengus.adapter.rest.dto.MarathonStatsDto;
import app.oengus.application.OengusWebhookService;
import app.oengus.application.SubmissionService;
import app.oengus.adapter.rest.Views;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.net.URI;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static app.oengus.adapter.rest.helper.HeaderHelpers.cachingHeaders;

@Tag(name = "marathons-v1")
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/marathons")
public class MarathonController {
    private final MarathonDtoMapper mapper;
    private final UserSecurityPort securityPort;
    private final MarathonService marathonService;
    private final OengusWebhookService webhookService;
    private final SubmissionService submissionService;

    @PutMapping
    @RolesAllowed({"ROLE_USER"})
    @PreAuthorize("isAuthenticated() && !isBanned()")
    @Operation(hidden = true)
    public ResponseEntity<?> create(
        @RequestBody @Valid final MarathonCreateRequestDto createRequest, final BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }

        final Marathon marathon = new Marathon(
            createRequest.getId()
        );

        marathon.setCreator(this.securityPort.getAuthenticatedUser());

        this.mapper.applyCreateRequest(marathon, createRequest);

        final Marathon created = this.marathonService.create(marathon);

        if (created == null) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.created(URI.create(created.getId())).build();
    }

    @GetMapping("/{name}/exists")
    @PermitAll
    @Operation(hidden = true)
    public ResponseEntity<Map<String, Boolean>> exists(@PathVariable("name") final String name) {
        final Map<String, Boolean> validationErrors = new HashMap<>();
        validationErrors.put("exists", this.marathonService.exists(name));

        return ResponseEntity.ok()
            .cacheControl(CacheControl.noCache())
            .body(validationErrors);
    }

    @PermitAll
    @GetMapping("/{id}")
    @JsonView(Views.Public.class)
    @Operation(
        summary = "Get information about a marathon"/*,
        response = MarathonDto.class*/
    )
    public ResponseEntity<MarathonDto> get(@PathVariable("id") final String id) {
        final Optional<Marathon> optionalMarathon = this.marathonService.findById(id);

        if (optionalMarathon.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        final var marathon = optionalMarathon.get();

        final var dto = this.mapper.toDto(marathon);

        if (marathon.isHasDonations()) {
            // TODO: implement donations
            dto.setDonationsTotal(BigDecimal.ZERO);
        }

        final var currUserId = this.securityPort.getAuthenticatedUserId();

        if (currUserId > -1) {
            dto.setHasSubmitted(
                this.submissionService.userHasSubmitted(id, currUserId)
            );
        }

        return ResponseEntity.ok()
            .headers(cachingHeaders(5, true))
            .body(dto);

    }

    @PermitAll
    @GetMapping("/{id}/stats")
    @JsonView(Views.Internal.class)
    @Operation(
        summary = "Get stats about a marathon, has a 5 minute cache"/*,
        response = MarathonStatsDto.class*/
    )
    public ResponseEntity<MarathonStatsDto> getStats(@PathVariable("id") final String id) throws NotFoundException {
        if (!this.marathonService.exists(id)) {
            throw new NotFoundException("Marathon not found");
        }

        final var marathonStats = this.marathonService.getStats(id);

        if (marathonStats.isEmpty()) {
            throw new NotFoundException("Marathon not found");
        }

        return ResponseEntity.ok()
            .headers(cachingHeaders(5, false))
            .body(
                this.mapper.statsFromDomain(marathonStats.get())
            );

    }

    @GetMapping
    @PermitAll
    @Operation(summary = "Get marathons as shown on the front page. Map is composed of 3 keys :\n" +
        "- next: 5 earliest upcoming marathons\n" +
        "- open: all marathons with submissions open\n" +
        "- live: all currently live marathons\n" +
        "Has a 5 minute cache")
    public ResponseEntity<Map<String, List<MarathonBasicInfoDto>>> getMarathons() {
        final var next = this.marathonService.findNext();
        final var open = this.marathonService.findSubmitsOpen();
        final var live = this.marathonService.findLive();

        final Function<List<Marathon>, List<MarathonBasicInfoDto>> transform =
            (items) -> items.stream().map(this.mapper::toBasicInfo).toList();

        return ResponseEntity.ok()
            .headers(cachingHeaders(5, false))
            .body(
                Map.of(
                    "next", transform.apply(next),
                    "open", transform.apply(open),
                    "live", transform.apply(live)
                )
            );
    }

    @GetMapping("/moderated-by/me")
    @RolesAllowed({"ROLE_USER"})
    @PreAuthorize("isAuthenticated() && !isBanned()")
    @Operation(summary = "Returns marathons that are moderated by the currently logged-in user")
    public ResponseEntity<Map<String, List<MarathonBasicInfoDto>>> getMarathonsIModerate() {
        final var marathons = this.marathonService.findActiveMarathonsIModerate();

        return ResponseEntity.ok()
            .cacheControl(CacheControl.noCache())
            .body(Map.of(
                "marathons", marathons.stream().map(this.mapper::toBasicInfo).toList()
            ));
    }

    @GetMapping("/forDates")
    @PermitAll
    @Operation(summary = "Get marathons between given dates, has a 5 minute cache")
    public ResponseEntity<List<MarathonBasicInfoDto>> getMarathonsForDates(
        @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) final ZonedDateTime start,
        @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) final ZonedDateTime end,
        @RequestParam("zoneId") final String zoneId) {
        final var marathons = this.marathonService.findMarathonsForDates(start, end, zoneId);

        return ResponseEntity.ok()
            .headers(cachingHeaders(5, false))
            .body(
                marathons.stream().map(this.mapper::toBasicInfo).toList()
            );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated() && isMarathonAdmin(#id) && !isBanned()")
    @Operation(hidden = true)
    public ResponseEntity<?> delete(@PathVariable("id") final String id) throws NotFoundException {
        this.marathonService.delete(id);

        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}")
    @PreAuthorize("isAuthenticated() && canUpdateMarathon(#id) && !isBanned()")
    @Operation(hidden = true)
    public ResponseEntity<?> update(@PathVariable("id") final String id,
                                    @RequestBody @Valid final MarathonUpdateRequestDto patch,
                                    final BindingResult bindingResult) throws NotFoundException {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }

        final var marathon = this.marathonService.findById(id).orElseThrow(
            () -> new NotFoundException("Marathon not found")
        );

        this.mapper.applyUpdateRequest(marathon, patch);

        final String newMstdn = patch.getMastodon();

        if (newMstdn != null && newMstdn.isBlank()) {
            patch.setMastodon(null);
        }

        this.marathonService.update(id, marathon);

        return ResponseEntity.noContent().build();
    }

    // we're checking the webhook on the backend to ensure "localhost" will fail
    @GetMapping("/{id}/webhook")
    @PreAuthorize("isAuthenticated() && canUpdateMarathon(#id) && !isBanned()")
    @Operation(hidden = true)
    public ResponseEntity<?> isWebhookOnline(@PathVariable("id") final String id,
                                             @RequestParam("url") final String url) {
        final boolean isOnline = this.webhookService.sendPingEvent(url);
        if (isOnline) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/selections/publish")
    @PreAuthorize("isAuthenticated() && canUpdateMarathon(#id) && !isBanned()")
    @Operation(hidden = true)
    public ResponseEntity<?> publishSelection(@PathVariable("id") final String id) throws NotFoundException {
        final var marathon = this.marathonService.findById(id).orElseThrow(
            () -> new NotFoundException("Marathon not found")
        );

        marathon.setSelectionDone(true);
        marathon.setSubmissionsOpen(false);

        this.marathonService.update(id, marathon);

        return ResponseEntity.ok().build();
    }
}
