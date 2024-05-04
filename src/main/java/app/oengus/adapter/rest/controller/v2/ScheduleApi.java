package app.oengus.adapter.rest.controller.v2;

import app.oengus.adapter.rest.dto.BooleanStatusDto;
import app.oengus.adapter.rest.dto.DataListDto;
import app.oengus.adapter.rest.dto.v2.schedule.LineDto;
import app.oengus.adapter.rest.dto.v2.schedule.ScheduleDto;
import app.oengus.adapter.rest.dto.v2.schedule.ScheduleInfoDto;
import app.oengus.adapter.rest.dto.v2.schedule.request.LineUpdateRequestDto;
import app.oengus.adapter.rest.dto.v2.schedule.request.ScheduleUpdateRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@CrossOrigin(maxAge = 3600)
@Tag(name = "schedules-v2")
@RequestMapping("/v2/marathons/{marathonId}/schedules")
public interface ScheduleApi {
    @GetMapping
    @PreAuthorize("canUpdateMarathon(#marathonId) || isScheduleDone(#marathonId)")
    @Operation(
        summary = "Get all schedules for a marathon, has a 5 minute cache",
        responses = {
            @ApiResponse(
                description = "List of schedules for this marathon",
                responseCode = "200",
                content = @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = ScheduleInfoDto.class))
                )
            ),
            @ApiResponse(
                description = "Marathon not found",
                responseCode = "404"
            )
        }
    )
    ResponseEntity<DataListDto<ScheduleInfoDto>> findAllForMarathon(@PathVariable("marathonId") final String marathonId);

    @GetMapping("/{scheduleId}")
    @PreAuthorize("canUpdateMarathon(#marathonId) || isSchedulePublished(#marathonId, #scheduleId)")
    @Operation(
        summary = "Get the info of a schedule for a marathon by its id, has a 5 minute cache",
        responses = {
            @ApiResponse(
                description = "The requested schedule info",
                responseCode = "200",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ScheduleInfoDto.class)
                )
            ),
            @ApiResponse(
                description = "Marathon or schedule not found",
                responseCode = "404"
            )
        }
    )
    ResponseEntity<ScheduleInfoDto> findScheduleById(
        @PathVariable("marathonId") final String marathonId,
        @PathVariable("scheduleId") final int scheduleId
    );

    // TODO: make slug a query param instead?
    @GetMapping("/for-slug/{slug}")
    @PreAuthorize("canUpdateMarathon(#marathonId) || isSchedulePublished(#marathonId, #slug)")
    @Operation(
        summary = "Get a schedule by its slug, has a 5 minute cache",
        responses = {
            @ApiResponse(
                description = "The requested schedule",
                responseCode = "200",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ScheduleDto.class)
                )
            ),
            @ApiResponse(
                description = "Marathon or schedule not found",
                responseCode = "404"
            )
        }
    )
    ResponseEntity<ScheduleDto> findScheduleBySlug(
        @PathVariable("marathonId") final String marathonId,
        @PathVariable("slug") final String slug,
        @RequestParam(defaultValue = "false", required = false) boolean withCustomData
    );

    @GetMapping("/slug-exists")
    @PreAuthorize("!isBanned() && canUpdateMarathon(#marathonId)")
    @Operation(
        summary = "Check if a schedule slug exists in a marathon",
        responses = {
            @ApiResponse(
                description = "True if the slug exists, false otherwise",
                responseCode = "200",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = BooleanStatusDto.class)
                )
            ),
            @ApiResponse(
                description = "Marathon not found",
                responseCode = "404"
            )
        }
    )
    ResponseEntity<BooleanStatusDto> existsBySlug(
        @PathVariable("marathonId") final String marathonId,
        @RequestParam("slug") final String slug
    );

    @PostMapping
    @PreAuthorize("!isBanned() && canUpdateMarathon(#marathonId) && canCreateExtraSchedule(#marathonId)")
    @Operation(
        summary = "Create a new schedule in a marathon.",
        responses = {
            @ApiResponse(
                description = "Schedule created",
                responseCode = "200",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ScheduleInfoDto.class)
                )
            ),
            @ApiResponse(
                description = "Invalid data",
                responseCode = "422"
            ),
            @ApiResponse(
                description = "Non supporters can only create 1 schedule per marathon",
                responseCode = "406"
            )
        }
    )
    ResponseEntity<ScheduleInfoDto> createSchedule(
        @PathVariable("marathonId") final String marathonId,
        @RequestBody @Valid final ScheduleUpdateRequestDto body
    );

    @PatchMapping("/{scheduleId}")
    @PreAuthorize("!isBanned() && canUpdateMarathon(#marathonId)")
    @Operation(
        summary = "Update a schedule in a marathon.",
        responses = {
            @ApiResponse(
                description = "Schedule updated",
                responseCode = "200",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ScheduleInfoDto.class)
                )
            ),
            @ApiResponse(
                description = "Invalid data",
                responseCode = "422"
            ),
            @ApiResponse(
                description = "Schedule not found",
                responseCode = "404"
            )
        }
    )
    ResponseEntity<ScheduleInfoDto> updateSchedule(
        @PathVariable final String marathonId,
        @PathVariable final int scheduleId,
        @RequestBody @Valid final ScheduleUpdateRequestDto body
    );

    @PostMapping("/{scheduleId}/publish")
    @PreAuthorize("!isBanned() && canUpdateMarathon(#marathonId)")
    @Operation(
        summary = "Publish schedule {scheduleId}",
        responses = {
            @ApiResponse(
                description = "Schedule published",
                responseCode = "200",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = BooleanStatusDto.class)
                )
            ),
            @ApiResponse(
                description = "Schedule not found",
                responseCode = "404"
            )
        }
    )
    ResponseEntity<BooleanStatusDto> publishSchedule(
        @PathVariable final String marathonId,
        @PathVariable final int scheduleId
    );

    @DeleteMapping("/{scheduleId}")
    @PreAuthorize("!isBanned() && canUpdateMarathon(#marathonId)")
    @Operation(
        summary = "Delete a schedule in a marathon.",
        responses = {
            @ApiResponse(
                description = "Schedule deleted",
                responseCode = "200",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = BooleanStatusDto.class)
                )
            ),
            @ApiResponse(
                description = "Schedule not found",
                responseCode = "404"
            )
        }
    )
    ResponseEntity<BooleanStatusDto> deleteSchedule(
        @PathVariable final String marathonId,
        @PathVariable final int scheduleId
    );

    @GetMapping("/{scheduleId}/lines")
    @PreAuthorize("!isBanned() && canUpdateMarathon(#marathonId)")
    @Operation(
        summary = "Get the lines for a schedule, no cache is applied. Custom data is always sent.",
        responses = {
            @ApiResponse(
                description = "Lines for a schedule",
                responseCode = "200",
                content = @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = LineDto.class))
                )
            ),
            @ApiResponse(
                description = "Schedule not found",
                responseCode = "404"
            )
        }
    )
    ResponseEntity<DataListDto<LineDto>> getLinesForSchedule(
        @PathVariable final String marathonId,
        @PathVariable final int scheduleId
    );

    @PutMapping("/{scheduleId}/lines")
    @PreAuthorize("!isBanned() && canUpdateMarathon(#marathonId)")
    @Operation(
        summary = "Update the lines for a schedule. Custom data is always sent.",
        responses = {
            @ApiResponse(
                description = "Updated lines",
                responseCode = "200",
                content = @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = LineDto.class))
                )
            ),
            @ApiResponse(
                description = "Schedule not found",
                responseCode = "404"
            ),
            @ApiResponse(
                description = "You sent invalid data",
                responseCode = "422"
            )
        }
    )
    ResponseEntity<DataListDto<LineDto>> saveLinesForSchedule(
        @PathVariable final String marathonId,
        @PathVariable final int scheduleId,
        @RequestBody @Valid final LineUpdateRequestDto body
    );
}
