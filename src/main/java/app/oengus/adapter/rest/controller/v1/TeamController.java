package app.oengus.adapter.rest.controller.v1;

import app.oengus.adapter.rest.mapper.TeamDtoMapper;
import app.oengus.application.TeamService;
import app.oengus.adapter.rest.dto.TeamDto;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.tags.Tag;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@Tag(name = "teams-v1")
@Hidden
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/marathons/{marathonId}/teams")
public class TeamController {
    private final TeamService teamService;
    private final TeamDtoMapper mapper;

    @GetMapping
    @PreAuthorize("canHaveTeams() && isAuthenticated() && canUpdateMarathon(#marathonId) && !isBanned()")
    public ResponseEntity<?> listTeams(@PathVariable("marathonId") final String marathonId) {
        return ResponseEntity.ok(
            this.teamService.getAll(marathonId)
        );
    }

    @PostMapping
    @PreAuthorize("canHaveTeams() && isAuthenticated() && canUpdateMarathon(#marathonId) && !isBanned()")
    public ResponseEntity<?> createTeam(
        @PathVariable("marathonId") final String marathonId,
        @RequestBody @Valid final TeamDto teamdto,
        final BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }

        final var newTeam = this.mapper.toDomain(teamdto);

        newTeam.setMarathonId(marathonId);

        this.teamService.save(newTeam);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    @PreAuthorize("canHaveTeams() && isAuthenticated() && canUpdateTeam(#teamId) && !isBanned()")
    public ResponseEntity<?> updateTeam(
        @PathVariable("marathonId") final String marathonId, // not needed but required somehow
        @PathVariable("id") final int teamId,
        @RequestBody @Valid final TeamDto teamdto,
        final BindingResult bindingResult
    ) throws NotFoundException {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }

        final var currentTeam = this.teamService.findById(teamId).orElseThrow(
            () -> new NotFoundException("Team not found")
        );

        this.mapper.applyDTO(currentTeam, teamdto);

        return ResponseEntity.ok(
            this.teamService.save(currentTeam)
        );
    }
}
