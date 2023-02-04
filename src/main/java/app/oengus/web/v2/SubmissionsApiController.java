package app.oengus.web.v2;

import app.oengus.entity.dto.DataListDto;
import app.oengus.entity.dto.v2.marathon.CategoryDto;
import app.oengus.entity.dto.v2.marathon.GameDto;
import app.oengus.entity.dto.v2.marathon.SubmissionDto;
import app.oengus.service.CategoryService;
import app.oengus.service.ExportService;
import app.oengus.service.GameService;
import app.oengus.service.SubmissionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import static app.oengus.helper.HeaderHelpers.cachingHeaders;

@RestController("v2SubmissionController")
public class SubmissionsApiController implements SubmissionsApi {

    private final CategoryService categoryService;
    private final ExportService exportService;
    private final GameService gameService;
    private final SubmissionService submissionService;

    public SubmissionsApiController(CategoryService categoryService, GameService gameService, ExportService exportService, SubmissionService submissionService) {
        this.categoryService = categoryService;
        this.gameService = gameService;
        this.exportService = exportService;
        this.submissionService = submissionService;
    }

    @Override
    public ResponseEntity<DataListDto<SubmissionDto>> getAllSubmissionsToplevel(final String marathonId) {
        return ResponseEntity.ok()
            .headers(cachingHeaders(30, false))
            .body(new DataListDto<>(
                this.submissionService.getToplevelSubmissionsForMarathon(marathonId)
            ));
    }

    // TODO: do we really need the marathon id in the queries?
    // (technically we don't, but is it better for security?)
    @Override
    public ResponseEntity<DataListDto<GameDto>> getGamesForSubmission(final String marathonId, final int submissionId) {
        return ResponseEntity.ok()
            .headers(cachingHeaders(30, false))
            .body(new DataListDto<>(
                this.gameService.findBySubmissionId(marathonId, submissionId)
            ));
    }

    @Override
    public ResponseEntity<DataListDto<CategoryDto>> getCatgegoriesForGame(final String marathonId, final int submissionId, final int gameId) {
        return ResponseEntity.ok()
            .headers(cachingHeaders(30, false))
            .body(new DataListDto<>(
                this.categoryService.findByGameId(marathonId, submissionId, gameId)
            ));
    }
}
