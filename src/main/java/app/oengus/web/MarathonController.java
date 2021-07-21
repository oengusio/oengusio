package app.oengus.web;

import app.oengus.entity.dto.ApplicationDto;
import app.oengus.entity.dto.MarathonBasicInfoDto;
import app.oengus.entity.dto.MarathonDto;
import app.oengus.entity.model.Application;
import app.oengus.entity.model.Marathon;
import app.oengus.entity.model.User;
import app.oengus.helper.PrincipalHelper;
import app.oengus.service.MarathonService;
import app.oengus.service.OengusWebhookService;
import app.oengus.service.repository.ApplicationRepositoryService;
import app.oengus.spring.model.Views;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import javassist.NotFoundException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import java.net.URI;
import java.security.Principal;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static app.oengus.helper.PrincipalHelper.getUserFromPrincipal;

@Api
@RestController
@RequestMapping("/marathons")
public class MarathonController {

    private final MarathonService marathonService;
    private final OengusWebhookService webhookService;
    private final ApplicationRepositoryService applicationRepositoryService;

    @Autowired
    public MarathonController(
        final MarathonService marathonService, final OengusWebhookService webhookService,
        final ApplicationRepositoryService applicationRepositoryService
    ) {
        this.marathonService = marathonService;
        this.webhookService = webhookService;
        this.applicationRepositoryService = applicationRepositoryService;
    }

    @PutMapping
    @RolesAllowed({"ROLE_USER"})
    @PreAuthorize("!isBanned()")
    @ApiIgnore
    public ResponseEntity<?> create(@RequestBody @Valid final Marathon marathon, final Principal principal,
                                    final BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }
        final Marathon created = this.marathonService.create(marathon,
            PrincipalHelper.getUserFromPrincipal(principal));
        if (created != null) {
            return ResponseEntity.created(URI.create(created.getId())).build();
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @GetMapping("/{name}/exists")
    @PermitAll
    @ApiIgnore
    public ResponseEntity<Map<String, Boolean>> exists(@PathVariable("name") final String name) {
        final Map<String, Boolean> validationErrors = new HashMap<>();
        validationErrors.put("exists", this.marathonService.exists(name));

        return ResponseEntity.ok(validationErrors);
    }

    @GetMapping("/{id}")
    @JsonView(Views.Public.class)
    @PermitAll
    @ApiOperation(value = "Get information about a marathon",
        response = MarathonDto.class)
    public ResponseEntity<MarathonDto> get(@PathVariable("id") final String id) throws NotFoundException {
        final MarathonDto marathon = this.marathonService.findOne(id);
        return ResponseEntity.ok().cacheControl(CacheControl.maxAge(1, TimeUnit.MINUTES)).body(marathon);

    }

    @GetMapping
    @PermitAll
    @ApiOperation(value = "Get marathons as shown on the front page. Map is composed of 3 keys :\n" +
        "- next: 5 earliest upcoming marathons\n" +
        "- open: all marathons with submissions open\n" +
        "- live: all currently live marathons")
    public ResponseEntity<Map<String, List<MarathonBasicInfoDto>>> getMarathons() {
        final Map<String, List<MarathonBasicInfoDto>> marathons = this.marathonService.findMarathons();
        return ResponseEntity.ok().cacheControl(CacheControl.maxAge(1, TimeUnit.MINUTES)).body(marathons);
    }

    @GetMapping("/forDates")
    @PermitAll
    @ApiOperation(value = "Get marathons between given dates")
    public ResponseEntity<List<MarathonBasicInfoDto>> getMarathonsForDates(
        @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) final ZonedDateTime start,
        @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) final ZonedDateTime end,
        @RequestParam("zoneId") final String zoneId) {
        final List<MarathonBasicInfoDto> marathons = this.marathonService.findMarathonsForDates(start, end, zoneId);
        return ResponseEntity.ok().cacheControl(CacheControl.maxAge(1, TimeUnit.MINUTES)).body(marathons);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated() && canUpdateMarathon(#id) && !isBanned()")
    @ApiIgnore
    public ResponseEntity<?> delete(@PathVariable("id") final String id) throws NotFoundException {
        this.marathonService.delete(id);

        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}")
    @PreAuthorize("isAuthenticated() && canUpdateMarathon(#id) && !isBanned()")
    @ApiIgnore
    public ResponseEntity<?> update(@PathVariable("id") final String id,
                                    @RequestBody @Valid final Marathon patch,
                                    final BindingResult bindingResult) throws NotFoundException {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }

        this.marathonService.update(id, patch);

        return ResponseEntity.noContent().build();
    }

    // we're checking the webhook on the backend to ensure "localhost" will fail
    @GetMapping("/{id}/webhook")
    @PreAuthorize("isAuthenticated() && canUpdateMarathon(#id) && !isBanned()")
    @ApiIgnore
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
    @ApiIgnore
    public ResponseEntity<?> publishSchedule(@PathVariable("id") final String id) throws NotFoundException {
        // make a fake marathon so we don't update the real one
        final Marathon marathon = new Marathon();

        BeanUtils.copyProperties(
            this.marathonService.getById(id),
            marathon
        );

        marathon.setSelectionDone(true);
        marathon.setSubmitsOpen(false);

        this.marathonService.update(id, marathon);

        return ResponseEntity.ok().build();
    }

    @ApiIgnore
    @GetMapping("/{id}/applications")
    @RolesAllowed({"ROLE_USER"})
    @JsonView(Views.Public.class)
    @PreAuthorize("isAuthenticated() && canUpdateMarathon(#id) && !isBanned()")
    public ResponseEntity<?> getOwnApplicationInfo(@PathVariable("id") final String id, final Principal principal) {
        final List<Application> applications = this.applicationRepositoryService.getApplications(id);

        return ResponseEntity.ok(applications);
    }

    // TODO: seperate route for updating status
    @ApiIgnore
    @PostMapping("/{id}/applications")
    @RolesAllowed({"ROLE_USER"})
    @JsonView(Views.Public.class)
    @PreAuthorize("isAuthenticated() && !isBanned()")
    public ResponseEntity<?> createApplication(
        @PathVariable("id") final String id,
        @RequestBody @Valid ApplicationDto applciation,
        final BindingResult bindingResult,
        final Principal principal
    ) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }

        final Marathon marathon = new Marathon();
        marathon.setId(id);
        final User user = getUserFromPrincipal(principal);

        this.applicationRepositoryService.updateApplication(
            user,
            marathon,
            applciation
        );

        return ResponseEntity.noContent().build();
    }
}
