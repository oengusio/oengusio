package app.oengus.adapter.rest.controller.v2;

import app.oengus.adapter.rest.dto.v2.schedule.request.ScheduleUpdateRequestDto;
import app.oengus.adapter.rest.mapper.ScheduleDtoMapper;
import app.oengus.adapter.rest.dto.DataListDto;
import app.oengus.adapter.rest.dto.v2.schedule.ScheduleDto;
import app.oengus.adapter.rest.dto.v2.schedule.ScheduleInfoDto;
import app.oengus.application.MarathonService;
import app.oengus.application.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;

import static app.oengus.adapter.rest.helper.HeaderHelpers.cachingHeaders;

@RequiredArgsConstructor
@RestController("v2ScheduleController")
public class ScheduleApiController implements ScheduleApi {
    private final MarathonService marathonService;
    private final ScheduleService scheduleService;
    private final ScheduleDtoMapper mapper;

    @Override
    public ResponseEntity<DataListDto<ScheduleInfoDto>> findAllForMarathon(final String marathonId) {
        if (!this.marathonService.exists(marathonId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Marathon not found");
        }

        final var schedules = this.scheduleService.findAllInfoByMarathon(marathonId);

        return ResponseEntity.ok()
            .headers(cachingHeaders(5, false))
            .body(new DataListDto<>(
                schedules.stream()
                    .map(this.mapper::infoFromSchedule)
                    .toList()
            ));
    }

    @Override
    public ResponseEntity<ScheduleDto> findScheduleById(
        final String marathonId, final int scheduleId, boolean withCustomData
    ) {
        if (!this.marathonService.exists(marathonId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Marathon not found");
        }

        final var schedule = this.scheduleService.findByScheduleId(marathonId, scheduleId, withCustomData).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Schedule not found")
        );

        return ResponseEntity.ok()
            .headers(cachingHeaders(5, false))
            .body(
                this.mapper.fromDomain(schedule)
            );
    }

    @Override
    public ResponseEntity<ScheduleInfoDto> createSchedule(String marathonId, ScheduleUpdateRequestDto body) {
        final var schedule = this.mapper.toDomain(body);

        final var savedSchedule = this.scheduleService.saveOrUpdate(marathonId, schedule);

        final var dto = this.mapper.infoFromSchedule(savedSchedule);

        return ResponseEntity.created(URI.create(
                "/v2/marathons/%s/schedules/%s".formatted(marathonId, savedSchedule.getId())
            ))
            .cacheControl(CacheControl.noCache())
            .body(dto);
    }
}
