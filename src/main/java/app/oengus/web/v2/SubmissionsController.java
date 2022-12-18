package app.oengus.web.v2;

import app.oengus.entity.dto.v2.marathon.CategoryDto;
import app.oengus.entity.dto.v2.marathon.SubmissionToplevelDto;
import app.oengus.entity.dto.v2.marathon.GameDto;
import app.oengus.service.ExportService;
import app.oengus.service.GameService;
import app.oengus.service.SubmissionService;
import app.oengus.spring.model.Views;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static app.oengus.helper.HeaderHelpers.cachingHeaders;

@RestController
@CrossOrigin(maxAge = 3600)
@Tag(name = "submissions-v2")
@RequestMapping("/v2/marathons/{marathonId}/submissions")
public class SubmissionsController {

    private final GameService gameService;
    private final ExportService exportService;
    private final SubmissionService submissionService;

    public SubmissionsController(GameService gameService, ExportService exportService, SubmissionService submissionService) {
        this.gameService = gameService;
        this.exportService = exportService;
        this.submissionService = submissionService;
    }

    @GetMapping
    @JsonView(Views.Public.class)
    @Operation(
        summary = "List submissions for marathon",
        description = "List all submissions for a marathon, top-level info only, has a 30 minute cache"
    )
    public ResponseEntity<SubmissionToplevelDto> getAllSubmissionsToplevel(
        @PathVariable("marathonId") final String marathonId
    ) {
        return ResponseEntity.ok()
            .headers(cachingHeaders(30, false))
            .body(
                this.submissionService.getToplevelSubmissionsForMarathon()
            );
    }

    @GetMapping("/users/{userId}/games")
    @JsonView(Views.Public.class)
    public ResponseEntity<List<GameDto>> getGamesForSubmission(
        @PathVariable("marathonId") final String marathonId,
        @PathVariable("userId") final int userId
    ) {
        return ResponseEntity.ok()
            .headers(cachingHeaders(30, false))
            .body(List.of());
    }

    @GetMapping("/users/{userId}/games/{gameId}/categories")
    @JsonView(Views.Public.class)
    public ResponseEntity<List<CategoryDto>> getCatgegoriesForGame(
        @PathVariable("marathonId") final String marathonId,
        @PathVariable("userId") final int userId,
        @PathVariable("gameId") final int gameId
    ) {
        return ResponseEntity.ok()
            .headers(cachingHeaders(30, false))
            .body(List.of());
    }
}
