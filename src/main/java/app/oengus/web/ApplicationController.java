package app.oengus.web;

import app.oengus.entity.constants.ApplicationStatus;
import app.oengus.entity.dto.ApplicationDto;
import app.oengus.service.ApplicationService;
import javassist.NotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.security.Principal;

import static app.oengus.helper.PrincipalHelper.getUserFromPrincipal;

@ApiIgnore
@RestController
@RequestMapping("/marathons/{marathonId}/teams/{teamId}/applications")
public class ApplicationController {
    private final ApplicationService applicationService;

    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    // admin
    @GetMapping
    @PreAuthorize("isAuthenticated() && canUpdateTeam(#teamId) && !isBanned()")
    public ResponseEntity<?> fetchAllApplications(@PathVariable("teamId") int teamId) {
        return ResponseEntity.ok(
            this.applicationService.getByTeam(teamId)
        );
    }

    public void batchUpdateApplicationStatuses() {}

    // user
    @GetMapping("/{userId}")
    @PreAuthorize("isAuthenticated() && (isSelf(#userId) || canUpdateTeam(#teamId)) && !isBanned()")
    public ResponseEntity<?> viewApplication(
        @PathVariable("teamId") int teamId,
        @PathVariable("userId") int userId
    ) throws NotFoundException {
        return ResponseEntity.ok(
            this.applicationService.getByTeamAndUser(teamId, userId)
        );
    }

    @PostMapping("/{userId}")
    @PreAuthorize("isAuthenticated() && isSelf(#userId) && applicationsOpen(#teamId) && !isBanned()")
    public ResponseEntity<?> createApplication(
        @PathVariable("teamId") int teamId,
        @PathVariable("userId") int userId,
        final Principal principal,
        @RequestBody @Valid ApplicationDto applicationDto,
        final BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }

        return ResponseEntity.accepted()
            .body(this.applicationService.createApplication(teamId, userId, applicationDto));
    }

    @PatchMapping("/{userId}")
    @PreAuthorize("isAuthenticated() && isSelf(#userId) && applicationsOpen(#teamId) && !isBanned()")
    public ResponseEntity<?> updateApplication(
        @PathVariable("teamId") int teamId,
        @PathVariable("userId") int userId,
        final Principal principal,
        @RequestBody @Valid ApplicationDto applicationDto,
        final BindingResult bindingResult
    ) throws NotFoundException {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }

        applicationDto.setStatus(null);

        return ResponseEntity.ok(
            this.applicationService.update(
                teamId, userId,
                getUserFromPrincipal(principal),
                applicationDto
            )
        );
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("isAuthenticated() && isSelf(#userId) && !isBanned()")
    public ResponseEntity<?> withdrawApplication(
        @PathVariable("teamId") int teamId,
        @PathVariable("userId") int userId,
        final Principal principal
    ) throws NotFoundException {
        final ApplicationDto applicationDto = new ApplicationDto();
        applicationDto.setStatus(ApplicationStatus.WITHDRAWN);

        this.applicationService.update(
            teamId, userId,
            getUserFromPrincipal(principal),
            applicationDto
        );

        return ResponseEntity.accepted().build();
    }
}
