package app.oengus.adapter.rest.controller.v2;

import app.oengus.adapter.rest.dto.BooleanStatusDto;
import app.oengus.adapter.rest.dto.DataListDto;
import app.oengus.adapter.rest.dto.v2.MarathonHomeDto;
import app.oengus.adapter.rest.dto.v2.marathon.MarathonSettingsDto;
import app.oengus.adapter.rest.dto.v2.marathon.QuestionDto;
import app.oengus.adapter.rest.dto.v2.marathon.request.ModeratorsUpdateRequest;
import app.oengus.adapter.rest.dto.v2.marathon.request.QuestionsUpdateRequest;
import app.oengus.adapter.rest.dto.v2.users.ProfileDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.PermitAll;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/{id}/settings")
    @PreAuthorize("isMarathonMod(#marathonId)")
    @Operation(
        summary = "Get the settings for a specific marathon",
        responses = {
            @ApiResponse(
                description = "Marathon settings",
                responseCode = "200",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = MarathonSettingsDto.class))
            )
        }
    )
    ResponseEntity<MarathonSettingsDto> getSettings(@PathVariable("id") final String marathonId);

    @PatchMapping("/{id}/settings")
    @PreAuthorize("canUpdateMarathon(#marathonId)")
    @Operation(
        summary = "Update the settings for a marathon",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = MarathonSettingsDto.class))
        ),
        responses = {
            @ApiResponse(
                description = "Updated marathon settings",
                responseCode = "200",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = MarathonSettingsDto.class))
            )
        }
    )
    ResponseEntity<MarathonSettingsDto> saveSettings(@PathVariable("id") final String marathonId, @RequestBody final MarathonSettingsDto patch);

    @GetMapping("/{id}/settings/moderators")
    @PreAuthorize("isMarathonMod(#marathonId)")
    @Operation(
        summary = "List the moderators for a marathon",
        responses = {
            @ApiResponse(
                description = "Moderators for the marathon",
                responseCode = "200",
                content = @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = ProfileDto.class))
                )
            )
        }
    )
    ResponseEntity<DataListDto<ProfileDto>> getModerators(@PathVariable("id") final String marathonId);

    @PutMapping("/{id}/settings/moderators")
    @PreAuthorize("canUpdateMarathon(#marathonId)")
    @Operation(
        summary = "Update the moderators for a marathon",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ModeratorsUpdateRequest.class)
            )
        ),
        responses = {
            @ApiResponse(
                description = "Status: true, Moderators have been updated",
                responseCode = "200",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = BooleanStatusDto.class)
                )
            )
        }
    )
    ResponseEntity<BooleanStatusDto> updateModerators(@PathVariable("id") final String marathonId, @RequestBody final ModeratorsUpdateRequest body);

    @DeleteMapping("/{id}/settings/moderators/{userId}")
    @PreAuthorize("canUpdateMarathon(#marathonId)")
    ResponseEntity<BooleanStatusDto> removeModerator(@PathVariable("id") final String marathonId, @PathVariable("userId") final int userId);

    @GetMapping("/{id}/settings/questions")
    @PreAuthorize("isMarathonMod(#marathonId)")
    @Operation(
        summary = "List the questions for a marathon",
        responses = {
            @ApiResponse(
                description = "Questions for the marathon",
                responseCode = "200",
                content = @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = QuestionDto.class))
                )
            )
        }
    )
    ResponseEntity<DataListDto<QuestionDto>> getQuestions(@PathVariable("id") final String marathonId);

    @PutMapping("/{id}/settings/questions")
    @PreAuthorize("canUpdateMarathon(#marathonId)")
    @Operation(
        summary = "Update the questions for a marathon",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = QuestionsUpdateRequest.class)
            )
        ),
        responses = {
            @ApiResponse(
                description = "Status: true, Questions have been updated",
                responseCode = "200",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = BooleanStatusDto.class)
                )
            )
        }
    )
    ResponseEntity<BooleanStatusDto> updateQuestions(@PathVariable("id") final String marathonId, @RequestBody final QuestionsUpdateRequest body);

    @DeleteMapping("/{id}/settings/questions/{questionId}")
    @PreAuthorize("canUpdateMarathon(#marathonId)")
    ResponseEntity<BooleanStatusDto> removeQuestion(@PathVariable("id") final String marathonId, @PathVariable("questionId") final int questionId);
}
