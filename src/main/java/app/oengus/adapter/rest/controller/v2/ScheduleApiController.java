package app.oengus.adapter.rest.controller.v2;

import app.oengus.adapter.rest.dto.BooleanStatusDto;
import app.oengus.adapter.rest.dto.DataListDto;
import app.oengus.adapter.rest.dto.v2.schedule.ScheduleInfoDto;
import app.oengus.adapter.rest.dto.v2.schedule.request.ScheduleUpdateRequestDto;
import app.oengus.adapter.rest.mapper.ScheduleDtoMapper;
import app.oengus.application.MarathonService;
import app.oengus.application.ScheduleService;
import app.oengus.domain.exception.schedule.ScheduleNotFoundException;
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
    public ResponseEntity<ScheduleInfoDto> findScheduleById(
        final String marathonId, final int scheduleId
    ) {
        if (!this.marathonService.exists(marathonId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Marathon not found");
        }

        final var schedule = this.scheduleService.findInfoByScheduleId(marathonId, scheduleId).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Schedule not found")
        );

        return ResponseEntity.ok()
            .headers(cachingHeaders(5, false))
            .body(
                this.mapper.infoFromSchedule(schedule)
            );
    }

    @Override
    public ResponseEntity<BooleanStatusDto> existsBySlug(String marathonId, String slug) {
        final var exists = this.scheduleService.hasUsedSlug(marathonId, slug);

        return ResponseEntity.ok()
            .cacheControl(CacheControl.noCache())
            .body(new BooleanStatusDto(exists));
    }

    @Override
    public ResponseEntity<ScheduleInfoDto> createSchedule(String marathonId, ScheduleUpdateRequestDto body) {
        final var schedule = this.mapper.toDomain(body);

        // TODO: Check if the slug is unique

        final var savedSchedule = this.scheduleService.saveOrUpdate(marathonId, schedule);

        final var dto = this.mapper.infoFromSchedule(savedSchedule);

        return ResponseEntity.created(URI.create(
                "/v2/marathons/%s/schedules/%s".formatted(marathonId, savedSchedule.getId())
            ))
            .cacheControl(CacheControl.noCache())
            .body(dto);
    }

    @Override
    public ResponseEntity<ScheduleInfoDto> updateSchedule(String marathonId, int scheduleId, ScheduleUpdateRequestDto body) {
        final var schedule = this.scheduleService.findByScheduleId(marathonId, scheduleId, false)
            .orElseThrow(ScheduleNotFoundException::new);

        this.mapper.applyPatch(schedule, body);

        final var savedSchedule = this.scheduleService.saveOrUpdate(marathonId, schedule);
        final var dto = this.mapper.infoFromSchedule(savedSchedule);

        return ResponseEntity.ok()
            .cacheControl(CacheControl.noCache())
            .body(dto);
    }
}
