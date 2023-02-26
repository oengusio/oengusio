package app.oengus.web.v2;

import app.oengus.entity.dto.DataListDto;
import app.oengus.entity.dto.v2.marathon.CategoryDto;
import app.oengus.entity.dto.v2.marathon.GameDto;
import app.oengus.entity.dto.v2.marathon.SubmissionDto;
import app.oengus.spring.model.Views;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@CrossOrigin(maxAge = 3600)
@Tag(name = "submissions-v2")
@RequestMapping("/v2/marathons/{marathonId}/submissions")
public interface SubmissionsApi {
    @GetMapping
    @JsonView(Views.Public.class)
    @Operation(
        summary = "List submissions for marathon",
        description = "List all submissions for a marathon, top-level info only, has a 30 minute cache"
    )
    ResponseEntity<DataListDto<SubmissionDto>> getAllSubmissionsToplevel(
        @PathVariable("marathonId") final String marathonId
    );

    @GetMapping("/{submissionId}/games")
    @JsonView(Views.Public.class)
    @Operation(
        summary = "List games for a submission",
        description = "List all games for a specific submission, has a 30 minute cache"
    )
    ResponseEntity<DataListDto<GameDto>> getGamesForSubmission(
        @PathVariable("marathonId") final String marathonId,
        @PathVariable("submissionId") final int submissionId
    );

    @GetMapping("/{submissionId}/games/{gameId}/categories")
    @JsonView(Views.Public.class)
    @Operation(
        summary = "List categories for a game",
        description = "List all categories for a specific game, has a 30 minute cache"
    )
    ResponseEntity<DataListDto<CategoryDto>> getCatgegoriesForGame(
        @PathVariable("marathonId") final String marathonId,
        @PathVariable("submissionId") final int submissionId,
        @PathVariable("gameId") final int gameId
    );
}
