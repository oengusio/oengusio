package app.oengus.web.v2;

import app.oengus.entity.dto.DataListDto;
import app.oengus.entity.dto.v2.schedule.ScheduleDto;
import app.oengus.service.ExportService;
import app.oengus.service.MarathonService;
import app.oengus.service.ScheduleService;
import javassist.NotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import static app.oengus.helper.HeaderHelpers.cachingHeaders;

@RestController("v2ScheduleController")
public class ScheduleApiController implements ScheduleApi {
    private final MarathonService marathonService;
    private final ScheduleService scheduleService;
    private final ExportService exportService;

    public ScheduleApiController(MarathonService marathonService, ScheduleService scheduleService, ExportService exportService) {
        this.marathonService = marathonService;
        this.scheduleService = scheduleService;
        this.exportService = exportService;
    }

    @Override
    public ResponseEntity<DataListDto<ScheduleDto>> findAllForMarathon(final String marathonId, boolean withCustomData) throws NotFoundException {
        if (!this.marathonService.exists(marathonId)) {
            throw new NotFoundException("Marathon not found");
        }

        return ResponseEntity.ok()
            .headers(cachingHeaders(5, false))
            .body(new DataListDto<>(
                this.scheduleService.findAllByMarathon(marathonId, withCustomData)
            ));
    }

    @Override
    public ResponseEntity<ScheduleDto> findScheduleById(
        final String marathonId, final int scheduleId, boolean withCustomData
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
