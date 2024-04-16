package app.oengus.adapter.rest.controller.v1;

import app.oengus.entity.dto.ScheduleTickerDto;
import app.oengus.entity.dto.V1ScheduleDto;
import app.oengus.entity.model.Schedule;
import app.oengus.application.ExportService;
import app.oengus.service.ScheduleService;
import app.oengus.spring.model.Views;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import javassist.NotFoundException;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.function.BiFunction;

import static app.oengus.helper.HeaderHelpers.cachingHeaders;

@RestController
@CrossOrigin(maxAge = 3600)
@Tag(name = "schedules-v1")
@RequestMapping("/v1/marathons/{marathonId}/schedule")
public class ScheduleController {
    private final ScheduleService scheduleService;
    private final ExportService exportService;

    public ScheduleController(ScheduleService scheduleService, ExportService exportService) {
        this.scheduleService = scheduleService;
        this.exportService = exportService;
    }

    @GetMapping
    @PreAuthorize("(canUpdateMarathon(#marathonId) || isScheduleDone(#marathonId))")
    @JsonView(Views.Public.class)
    @Operation(summary = "Get schedule for a marathon, has a 5 minute cache"/*, response = Schedule.class*/)
    public ResponseEntity<V1ScheduleDto> findAllForMarathon(@PathVariable("marathonId") final String marathonId,
                                                            @RequestParam(defaultValue = "false", required = false) boolean withCustomData) {
        return ResponseEntity.ok()
            .headers(cachingHeaders(5, false))
            .body(this.scheduleService.findByMarathonCustomDataControl(marathonId, withCustomData));
    }

    @GetMapping("/ticker")
    @PreAuthorize("isScheduleDone(#marathonId)")
    @JsonView(Views.Public.class)
    @Operation(summary = "Get a ticker for this schedule, displaying the previous, current and next lines. Cache is present but varies"/*,
        response = ScheduleTickerDto.class*/)
    public ResponseEntity<ScheduleTickerDto> getTicker(@PathVariable("marathonId") final String marathonId,
                                                @RequestParam(defaultValue = "false", required = false) boolean withCustomData) throws NotFoundException {
        return ResponseEntity.ok()
            .headers(cachingHeaders(1, false))
            .body(this.scheduleService.getForTicker(marathonId, withCustomData));
    }

    ///////////////
    // ADMIN ROUTES

    // TODO: make better route in v2
    @GetMapping("/admin")
    @PreAuthorize("canUpdateMarathon(#marathonId)")
    @JsonView(Views.Public.class)
    @Operation(hidden = true)
    public ResponseEntity<V1ScheduleDto> findAllForMarathonAdmin(@PathVariable("marathonId") final String marathonId) {
        return ResponseEntity.ok()
            .cacheControl(CacheControl.noCache()) // Always fetch custom data for the admin page
            .body(this.scheduleService.findByMarathonCustomDataControl(marathonId, true));
    }

    @PutMapping
    @PreAuthorize("!isBanned() && canUpdateMarathon(#marathonId)")
    @Operation(hidden = true)
    public ResponseEntity<?> saveOrUpdate(@PathVariable("marathonId") final String marathonId,
                                          @RequestBody final Schedule schedule) throws NotFoundException {
        this.scheduleService.saveOrUpdate(marathonId, schedule);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/export")
    @PreAuthorize("canUpdateMarathon(#marathonId) || isScheduleDone(#marathonId)")
    @Operation(summary = "Export schedule to format specified in parameter. Available formats : csv, json, ics. Cached for 30 minutes.")
    public void exportAllForMarathon(@PathVariable("marathonId") final String marathonId,
                                     @RequestParam("format") final String format,
                                     @RequestParam("zoneId") final String zoneId,
                                     @RequestParam("locale") final String locale,
                                     final HttpServletResponse response) throws IOException, NotFoundException {
        final BiFunction<String, String, Void> addDefaultHeaders = (contentType, extension) -> {
            response.setContentType(contentType);
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            cachingHeaders(30).toSingleValueMap().forEach(response::setHeader);
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + marathonId + "-schedule." + extension + "\"");

            return null;
        };

        // Oh the urge to use reflection to clean this up :(
        switch (format.toLowerCase()) {
            case "csv" -> {
                try (final Writer writer = this.exportService.exportScheduleToCsv(marathonId, zoneId, locale)) {
                    final String export = writer.toString();
                    addDefaultHeaders.apply("text/csv", "csv");
                    response.getWriter().write(export);
                }
            }
            case "json" -> {
                try (final Writer writer = this.exportService.exportScheduleToJson(marathonId, zoneId, locale)) {
                    final String export = writer.toString();
                    addDefaultHeaders.apply("application/json", "json");
                    response.getWriter().write(export);}
            }
            case "ics" -> {
                try (final Writer writer = this.exportService.exportScheduleToIcal(marathonId, zoneId, locale)) {
                    final String export = writer.toString();
                    addDefaultHeaders.apply("text/calendar", "ics");
                    response.getWriter().write(export);
                }
            }
            default -> throw new NotFoundException("Format not found");
        }

    }
}
