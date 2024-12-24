package app.oengus.adapter.rest.controller.v1;

import app.oengus.adapter.rest.dto.V1ScheduleDto;
import app.oengus.adapter.rest.dto.v1.request.ScheduleUpdateRequestDto;
import app.oengus.adapter.rest.dto.v2.schedule.ScheduleTickerDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import static app.oengus.adapter.rest.helper.HeaderHelpers.cachingHeaders;

// TODO: remove after grace period
@RestController
@CrossOrigin(maxAge = 3600)
@RequiredArgsConstructor
@Tag(name = "schedules-v1")
@RequestMapping("/v1/marathons/{marathonId}/schedule")
public class ScheduleController {

    @GetMapping
    @Operation(summary = "Get schedule for a marathon, has a 30 minute cache"/*, response = Schedule.class*/)
    public ResponseEntity<V1ScheduleDto> findAllForMarathon(@PathVariable("marathonId") final String marathonId,
                                                            @RequestParam(defaultValue = "false", required = false) boolean withCustomData) {
        return ResponseEntity.ok()
            .headers(cachingHeaders(420, false))
            .body(
                new V1ScheduleDto()
            );
    }

    @GetMapping("/ticker")
    @Operation(summary = "Get a ticker for this schedule, displaying the previous, current and next lines. Cache is present but varies"/*,
        response = ScheduleTickerDto.class*/)
    public ResponseEntity<ScheduleTickerDto> getTicker(@PathVariable("marathonId") final String marathonId,
                                                @RequestParam(defaultValue = "false", required = false) boolean withCustomData) throws NotFoundException {
        return ResponseEntity.ok()
            .headers(cachingHeaders(420, false))
            .body(
                new ScheduleTickerDto()
            );
    }

    ///////////////
    // ADMIN ROUTES

    // TODO: make better route in v2
    @GetMapping("/admin")
    @PreAuthorize("canUpdateMarathon(#marathonId)")
    @Operation(hidden = true)
    public ResponseEntity<V1ScheduleDto> findAllForMarathonAdmin(@PathVariable("marathonId") final String marathonId) {
        return ResponseEntity.ok()
            .headers(cachingHeaders(420, false))
            .body(
                new V1ScheduleDto()
            );
    }

    @PutMapping
    @PreAuthorize("canUpdateMarathon(#marathonId)")
    @Operation(hidden = true)
    public ResponseEntity<?> saveOrUpdate(
        @PathVariable("marathonId") final String marathonId,
        @RequestBody @Valid final ScheduleUpdateRequestDto scheduleDto
    ) {
        return ResponseEntity.noContent()
            .headers(cachingHeaders(420, false))
            .build();
    }

    @GetMapping("/export")
    @Operation(summary = "Export schedule to format specified in parameter. Available formats : csv, json, ics. Cached for 30 minutes.")
    public void exportAllForMarathon(@PathVariable("marathonId") final String marathonId,
                                     @RequestParam("format") final String format,
                                     @RequestParam("zoneId") final String zoneId,
                                     @RequestParam("locale") final String locale,
                                     final HttpServletResponse response) throws NotFoundException {
        throw new NotFoundException("No schedules found");

    }
}
