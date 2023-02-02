package app.oengus.web.v2;

import app.oengus.entity.dto.DataListDto;
import app.oengus.entity.dto.v2.schedule.ScheduleDto;
import app.oengus.service.ExportService;
import app.oengus.service.MarathonService;
import app.oengus.service.ScheduleService;
import app.oengus.spring.model.Views;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import javassist.NotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static app.oengus.helper.HeaderHelpers.cachingHeaders;

@CrossOrigin(maxAge = 3600)
@Tag(name = "schedules-v2")
@RestController("v2ScheduleController")
@RequestMapping("/v2/marathons/{marathonId}/schedules")
public class ScheduleController {
    private final MarathonService marathonService;
    private final ScheduleService scheduleService;
    private final ExportService exportService;

    public ScheduleController(MarathonService marathonService, ScheduleService scheduleService, ExportService exportService) {
        this.marathonService = marathonService;
        this.scheduleService = scheduleService;
        this.exportService = exportService;
    }

    // TODO: make interfaces with just the methods to clean up the controllers
    @GetMapping
    @JsonView(Views.Public.class)
    @PreAuthorize("canUpdateMarathon(#marathonId) || isScheduleDone(#marathonId)")
    @Operation(
        summary = "Get all schedules for a marathon, has a 5 minute cache",
        responses = {
            @ApiResponse(
                description = "List of schedules for this marathon",
                responseCode = "200",
                content = @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = ScheduleDto.class))
                )
            ),
            @ApiResponse(
                description = "Marathon not found",
                responseCode = "404"
            )
        }
    )
    public ResponseEntity<DataListDto<ScheduleDto>> findAllForMarathon(@PathVariable("marathonId") final String marathonId,
                                                                       @RequestParam(defaultValue = "false", required = false) boolean withCustomData) throws NotFoundException {
        if (!this.marathonService.exists(marathonId)) {
            throw new NotFoundException("Marathon not found");
        }

        return ResponseEntity.ok()
            .headers(cachingHeaders(5, false))
            .body(new DataListDto<>(
                this.scheduleService.findAllByMarathon(marathonId, withCustomData)
            ));
    }

    @GetMapping("/{scheduleId}")
    @JsonView(Views.Public.class)
    @PreAuthorize("canUpdateMarathon(#marathonId) || isScheduleDone(#marathonId)")@Operation(
        summary = "Get a schedule for a marathon by its id, has a 5 minute cache",
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
                description = "Marathon not found",
                responseCode = "404"
            )
        }
    )
    public ResponseEntity<ScheduleDto> findScheduleById(
        @PathVariable("marathonId") final String marathonId,
        @PathVariable("scheduleId") final int scheduleId,
        @RequestParam(defaultValue = "false", required = false) boolean withCustomData
    ) throws NotFoundException {
        if (!this.marathonService.exists(marathonId)) {
            throw new NotFoundException("Marathon not found");
        }

        return ResponseEntity.ok()
            .headers(cachingHeaders(5, false))
            .body(
                this.scheduleService.findByScheduleId(marathonId, scheduleId, withCustomData)
            );
    }
}
