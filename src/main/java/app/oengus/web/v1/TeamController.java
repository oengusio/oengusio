package app.oengus.web.v1;

import app.oengus.entity.dto.TeamDto;
import app.oengus.service.repository.TeamRepositoryService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.tags.Tag;
import javassist.NotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Tag(name = "teams-v1")
@Hidden
@RestController
@RequestMapping({"/v1/marathons/{marathonId}/teams", "/marathons/{marathonId}/teams"})
public class TeamController {
    private final TeamRepositoryService teamRepositoryService;

    public TeamController(TeamRepositoryService teamRepositoryService) {
        this.teamRepositoryService = teamRepositoryService;
    }

    @GetMapping
    @PreAuthorize("canHaveTeams() && isAuthenticated() && canUpdateMarathon(#marathonId) && !isBanned()")
    public ResponseEntity<?> listTeams(@PathVariable("marathonId") final String marathonId) {
        return ResponseEntity.ok(
            this.teamRepositoryService.getAll(marathonId)
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

        this.teamRepositoryService.create(marathonId, teamdto);

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

        return ResponseEntity.ok(
            this.teamRepositoryService.update(teamId, teamdto)
        );
    }
}
