package app.oengus.web.v2;

import app.oengus.service.ExportService;
import app.oengus.service.GameService;
import app.oengus.service.SubmissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/top-level")
    @Operation(
        summary = "List submissions for marathon",
        description = "List all submissions for a marathon, top-level info only, has a 30 minute cache"
    )
    public ResponseEntity<?> getAllSubmissionsToplevel(
        @PathVariable("marathonId") final String marathonId
    ) {
        return ResponseEntity.ok()
            .headers(cachingHeaders(30, false))
            .body("TODO");
    }
}
