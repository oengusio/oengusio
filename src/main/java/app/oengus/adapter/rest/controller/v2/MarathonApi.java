package app.oengus.adapter.rest.controller.v2;

import app.oengus.adapter.rest.dto.BooleanStatusDto;
import app.oengus.adapter.rest.dto.v2.MarathonHomeDto;
import app.oengus.adapter.rest.dto.v2.marathon.MarathonSettingsDto;
import app.oengus.adapter.rest.dto.v2.marathon.request.ModeratorsUpdateRequest;
import app.oengus.adapter.rest.dto.v2.marathon.QuestionDto;
import app.oengus.adapter.rest.dto.v2.marathon.request.QuestionsUpdateRequest;
import app.oengus.adapter.rest.dto.v2.users.ProfileDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.PermitAll;
import java.util.List;

@Tag(name = "marathons-v2")
@CrossOrigin(maxAge = 3600)
@RequestMapping("/v2/marathons")
public interface MarathonApi {

    @PermitAll
    @GetMapping("/for-home")
    @Operation(
        summary = "Get marathons as shown on the front page. Has a 10 minute cache",
        responses = {
            @ApiResponse(
                description = "Marathons as shown on the front page.",
                responseCode = "200",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = MarathonHomeDto.class))
            )
        }
    )
    ResponseEntity<MarathonHomeDto> getMarathonsForHome();

    // /marathons/{id}/settings GET/PUT
    // /marathons/{id}/settings/moderators GET/PUT/DELETE
    // /marathons/{id}/settings/questions GET/PUT/DELETE

    @GetMapping("/{id}/settings")
    @PreAuthorize("canUpdateMarathon(#marathonId)")
    ResponseEntity<MarathonSettingsDto> getSettings(@PathVariable("id") final String marathonId);

    @PutMapping("/{id}/settings")
    @PreAuthorize("canUpdateMarathon(#marathonId)")
    ResponseEntity<MarathonSettingsDto> saveSettings(@PathVariable("id") final String marathonId, @RequestBody final MarathonSettingsDto patch);

    @GetMapping("/{id}/settings/moderators")
    @PreAuthorize("canUpdateMarathon(#marathonId)")
    ResponseEntity<List<ProfileDto>> getModerators(@PathVariable("id") final String marathonId);

    @PutMapping("/{id}/settings/moderators")
    @PreAuthorize("canUpdateMarathon(#marathonId)")
    ResponseEntity<BooleanStatusDto> updateModerators(@PathVariable("id") final String marathonId, @RequestBody final ModeratorsUpdateRequest body);

    @DeleteMapping("/{id}/settings/moderators/{userId}")
    @PreAuthorize("canUpdateMarathon(#marathonId)")
    ResponseEntity<BooleanStatusDto> removeModerator(@PathVariable("id") final String marathonId, @PathVariable("userId") final int userId);

    @GetMapping("/{id}/settings/questions")
    @PreAuthorize("canUpdateMarathon(#marathonId)")
    ResponseEntity<List<QuestionDto>> getQuestions(@PathVariable("id") final String marathonId);

    @PutMapping("/{id}/settings/questions")
    @PreAuthorize("canUpdateMarathon(#marathonId)")
    ResponseEntity<BooleanStatusDto> updateQuestions(@PathVariable("id") final String marathonId, @RequestBody final QuestionsUpdateRequest body);

    @DeleteMapping("/{id}/settings/questions/{questionId}")
    @PreAuthorize("canUpdateMarathon(#marathonId)")
    ResponseEntity<BooleanStatusDto> removeQuestion(@PathVariable("id") final String marathonId, @PathVariable("questionId") final int questionId);
}
