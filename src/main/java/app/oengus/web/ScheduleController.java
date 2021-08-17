package app.oengus.web;

import app.oengus.entity.dto.schedule.ScheduleTickerDto;
import app.oengus.entity.dto.schedule.ScheduleDto;
import app.oengus.entity.model.Schedule;
import app.oengus.service.ExportService;
import app.oengus.service.ScheduleService;
import app.oengus.spring.model.Views;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping("/marathons/{marathonId}/schedule")
@Api
public class ScheduleController {
    private final ScheduleService scheduleService;
    private final ExportService exportService;

    @Autowired
    public ScheduleController(ScheduleService scheduleService, ExportService exportService) {
        this.scheduleService = scheduleService;
        this.exportService = exportService;
    }

    @GetMapping
    @PreAuthorize("(canUpdateMarathon(#marathonId) || isScheduleDone(#marathonId))")
    @JsonView(Views.Public.class)
    @ApiOperation(value = "Get schedule for a marathon",
        response = ScheduleDto.class)
    public ResponseEntity<?> findAllForMarathon(@PathVariable("marathonId") final String marathonId,
                                                @RequestHeader(value = "oengus-version", required = false) String version,
                                                @RequestParam(defaultValue = "false", required = false) boolean withCustomData)
        throws NotFoundException {
        // Return a new model for version 2
        if ("2".equals(version)) {
            return ResponseEntity.ok()
                .body(this.scheduleService.findByMarathonDto(marathonId, withCustomData));
        }

        return ResponseEntity.ok()
            .cacheControl(CacheControl.maxAge(1, TimeUnit.MINUTES))
            .body(this.scheduleService.findByMarathonCustomDataControl(marathonId, withCustomData));
    }

    @GetMapping("/ticker")
    @PreAuthorize("isScheduleDone(#marathonId)")
    @JsonView(Views.Public.class)
    @ApiOperation(value = "Get a ticker for this schedule, displaying the previous, current and next lines",
        response = ScheduleTickerDto.class)
    public ResponseEntity<?> getTicker(@PathVariable("marathonId") final String marathonId,
                                                @RequestParam(defaultValue = "false", required = false) boolean withCustomData) {
        return ResponseEntity.ok()
            .cacheControl(CacheControl.maxAge(1, TimeUnit.MINUTES))
            .body(this.scheduleService.getForTicker(marathonId, withCustomData));
    }

    @PutMapping
    @PreAuthorize("!isBanned() && canUpdateMarathon(#marathonId)")
    @ApiIgnore
    public ResponseEntity<?> saveOrUpdate(@PathVariable("marathonId") final String marathonId,
                                          @RequestBody final Schedule schedule) throws NotFoundException {
        this.scheduleService.saveOrUpdate(marathonId, schedule);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/export")
    @ApiOperation(value = "Export schedule to format specified in parameter. Available formats : csv, json, ics")
    public void exportAllForMarathon(@PathVariable("marathonId") final String marathonId,
                                     @RequestParam("format") final String format,
                                     @RequestParam("zoneId") final String zoneId,
                                     @RequestParam("locale") final String locale,
                                     final HttpServletResponse response) throws IOException {
        switch (format.toLowerCase()) {
            case "csv":
                response.setContentType("text/csv");
                response.setCharacterEncoding(StandardCharsets.UTF_8.name());
                response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"" + marathonId + "-schedule.csv\"");
                response.getWriter()
                    .write(this.exportService.exportScheduleToCsv(marathonId, zoneId, locale).toString());
                break;
            case "json":
                response.setContentType("text/plain");
                response.setCharacterEncoding(StandardCharsets.UTF_8.name());
                response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"" + marathonId + "-schedule.json\"");
                response.getWriter()
                    .write(this.exportService.exportScheduleToJson(marathonId, zoneId, locale).toString());
                break;
            case "ics":
                response.setContentType("text/calendar");
                response.setCharacterEncoding(StandardCharsets.UTF_8.name());
                response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"" + marathonId + "-schedule.ics\"");
                response.getWriter()
                    .write(this.exportService.exportScheduleToIcal(marathonId, zoneId, locale).toString());
                break;
            default:
                break;
        }

    }
}
