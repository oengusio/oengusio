package app.oengus.adapter.rest.controller.v2;

import app.oengus.adapter.rest.mapper.CategoryDtoMapper;
import app.oengus.adapter.rest.mapper.GameDtoMapper;
import app.oengus.adapter.rest.dto.v2.marathon.CategoryDataListDto;
import app.oengus.adapter.rest.dto.v2.marathon.GameDataListDto;
import app.oengus.adapter.rest.dto.v2.marathon.SubmissionDataListDto;
import app.oengus.application.CategoryService;
import app.oengus.application.ExportService;
import app.oengus.application.GameService;
import app.oengus.application.SubmissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import static app.oengus.adapter.rest.helper.HeaderHelpers.cachingHeaders;

@RequiredArgsConstructor
@RestController("v2SubmissionController")
public class SubmissionsApiController implements SubmissionsApi {
    private final CategoryService categoryService;
    private final ExportService exportService;
    private final GameService gameService;
    private final SubmissionService submissionService;
    private final CategoryDtoMapper categoryDtoMapper;
    private final GameDtoMapper gameDtoMapper;

    @Override
    public ResponseEntity<SubmissionDataListDto> getAllSubmissionsToplevel(final String marathonId) {
        return ResponseEntity.ok()
            .headers(cachingHeaders(30, false))
            .body(new SubmissionDataListDto(
                this.submissionService.getToplevelSubmissionsForMarathon(marathonId)
            ));
    }

    // TODO: do we really need the marathon id in the queries?
    // (technically we don't, but is it better for security?)
    @Override
    public ResponseEntity<GameDataListDto> getGamesForSubmission(final String marathonId, final int submissionId) {
        final var games = this.gameService.findBySubmissionId(marathonId, submissionId);

        return ResponseEntity.ok()
            .headers(cachingHeaders(30, false))
            .body(new GameDataListDto(
                games.stream().map(this.gameDtoMapper::fromDomain).toList()
            ));
    }

    @Override
    public ResponseEntity<CategoryDataListDto> getCatgegoriesForGame(final String marathonId, final int submissionId, final int gameId) {
        final var categories = this.categoryService.findByGameId(marathonId, submissionId, gameId);

        return ResponseEntity.ok()
            .headers(cachingHeaders(30, false))
            .body(new CategoryDataListDto(
                categories.stream()
                    .map(this.categoryDtoMapper::fromDomain)
                    .toList()
            ));
    }
}
