package app.oengus.adapter.rest.controller.v1;

import app.oengus.adapter.rest.dto.v1.V1ApplicationDto;
import app.oengus.adapter.rest.dto.v1.request.ApplicationCreateRequestDto;
import app.oengus.adapter.rest.mapper.ApplicationDtoMapper;
import app.oengus.domain.volunteering.Application;
import app.oengus.domain.volunteering.ApplicationStatus;
import app.oengus.application.ApplicationService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.tags.Tag;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@Hidden
@Tag(name = "applications-v1")
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/marathons/{marathonId}/teams/{teamId}/applications")
public class ApplicationController {
    private final ApplicationService applicationService;
    private final ApplicationDtoMapper mapper;

    // admin
    @GetMapping
    @PreAuthorize("isAuthenticated() && canUpdateTeam(#teamId) && !isBanned()")
    public ResponseEntity<List<V1ApplicationDto>> fetchAllApplications(@PathVariable("teamId") int teamId) {
        final var applications = this.applicationService.getByTeam(teamId);

        return ResponseEntity.ok(
            applications.stream()
                .map(this.mapper::fromDomainV1)
                .toList()
        );
    }

    public void batchUpdateApplicationStatuses() {}

    // user
    @GetMapping("/{userId}")
    @PreAuthorize("isAuthenticated() && (isSelf(#userId) || canUpdateTeam(#teamId)) && !isBanned()")
    public ResponseEntity<V1ApplicationDto> viewApplication(
        @PathVariable("teamId") int teamId,
        @PathVariable("userId") int userId
    ) throws NotFoundException {
        final var application = this.applicationService.getByTeamAndUser(teamId, userId);

        return ResponseEntity.ok(
            this.mapper.fromDomainV1(application)
        );
    }

    @PostMapping("/{userId}")
    @PreAuthorize("isAuthenticated() && isSelf(#userId) && applicationsOpen(#teamId) && !isBanned()")
    public ResponseEntity<V1ApplicationDto> createApplication(
        @PathVariable("teamId") int teamId,
        @PathVariable("userId") int userId,
        @RequestBody @Valid ApplicationCreateRequestDto applicationDto
    ) {
        final var newApplication = new Application(-1, userId, teamId);
        final var createdApplication = this.applicationService.createApplication(teamId, userId, newApplication);

        return ResponseEntity.accepted()
            .body(
                this.mapper.fromDomainV1(createdApplication)
            );
    }

    @PatchMapping("/{userId}")
    @PreAuthorize("isAuthenticated() && isSelf(#userId) && applicationsOpen(#teamId) && !isBanned()")
    public ResponseEntity<V1ApplicationDto> updateApplication(
        @PathVariable("teamId") int teamId,
        @PathVariable("userId") int userId,
        @RequestBody @Valid ApplicationCreateRequestDto applicationDto
    ) throws NotFoundException {
        final var oldApplication = this.applicationService.getByTeamAndUser(teamId, userId);

        this.mapper.applyPatch(oldApplication, applicationDto);

        final var updatedApplication = this.applicationService.save(oldApplication);

        return ResponseEntity.ok(
            this.mapper.fromDomainV1(updatedApplication)
        );
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("isAuthenticated() && isSelf(#userId) && !isBanned()")
    public ResponseEntity<?> withdrawApplication(
        @PathVariable("teamId") int teamId,
        @PathVariable("userId") int userId
    ) throws NotFoundException {
        this.applicationService.changeApplicationStatus(
            teamId, userId,
            ApplicationStatus.WITHDRAWN
        );

        return ResponseEntity.accepted().build();
    }
}
