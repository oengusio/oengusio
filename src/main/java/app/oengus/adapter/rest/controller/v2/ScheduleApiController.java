package app.oengus.adapter.rest.controller.v2;

import app.oengus.entity.dto.DataListDto;
import app.oengus.adapter.rest.dto.v2.schedule.ScheduleDto;
import app.oengus.adapter.rest.dto.v2.schedule.ScheduleInfoDto;
import app.oengus.service.MarathonService;
import app.oengus.service.ScheduleService;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import static app.oengus.helper.HeaderHelpers.cachingHeaders;

@RequiredArgsConstructor
@RestController("v2ScheduleController")
public class ScheduleApiController implements ScheduleApi {
    private final MarathonService marathonService;
    private final ScheduleService scheduleService;

    @Override
    public ResponseEntity<DataListDto<ScheduleInfoDto>> findAllForMarathon(final String marathonId) {
        if (!this.marathonService.exists(marathonId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Marathon not found");
        }

        return ResponseEntity.ok()
            .headers(cachingHeaders(5, false))
            .body(new DataListDto<>(
                this.scheduleService.findAllInfoByMarathon(marathonId)
            ));
    }

    @Override
    public ResponseEntity<ScheduleDto> findScheduleById(
        final String marathonId, final int scheduleId, boolean withCustomData
    ) {
        if (!this.marathonService.exists(marathonId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Marathon not found");
        }

        final ScheduleDto byScheduleId;
        try {
            byScheduleId = this.scheduleService.findByScheduleId(marathonId, scheduleId, withCustomData);
        } catch (NotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }

        return ResponseEntity.ok()
            .headers(cachingHeaders(5, false))
            .body(byScheduleId);
    }
}
