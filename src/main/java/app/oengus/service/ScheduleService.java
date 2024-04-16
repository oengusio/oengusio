package app.oengus.service;

import app.oengus.domain.marathon.Marathon;
import app.oengus.entity.dto.ScheduleLineDto;
import app.oengus.entity.dto.ScheduleTickerDto;
import app.oengus.entity.dto.V1ScheduleDto;
import app.oengus.adapter.rest.dto.v2.schedule.ScheduleDto;
import app.oengus.adapter.rest.dto.v2.schedule.ScheduleInfoDto;
import app.oengus.adapter.jpa.entity.MarathonEntity;
import app.oengus.entity.model.Schedule;
import app.oengus.entity.model.ScheduleLine;
import app.oengus.exception.schedule.EmptyScheduleException;
import app.oengus.service.mapper.ScheduleMapper;
import app.oengus.service.repository.MarathonRepositoryService;
import app.oengus.service.repository.ScheduleRepositoryService;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import javax.transaction.Transactional;
import java.time.ZonedDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduleService {
    private final ScheduleRepositoryService scheduleRepository;
    private final MarathonRepositoryService marathonRepositoryService;
    private final ScheduleMapper scheduleMapper;

    ///////////
    // v2 stuff

    public List<ScheduleInfoDto> findAllInfoByMarathon(final String marathonId) {
        return this.scheduleRepository.findAllByMarathon(MarathonEntity.ofId(marathonId))
            .stream()
            .map(scheduleMapper::toScheduleInfo)
            .toList();
    }

    public List<ScheduleDto> findAllByMarathon(final String marathonId, boolean withCustomData) {
        return this.scheduleRepository.findAllByMarathon(MarathonEntity.ofId(marathonId))
            .stream()
            .map((schedule) -> ScheduleDto.fromSchedule(schedule, withCustomData))
            .toList();
    }

    public ScheduleDto findByScheduleId(final String marathonId, final int scheduleId, boolean withCustomData) throws NotFoundException {
        final Schedule schedule = this.scheduleRepository.findById(MarathonEntity.ofId(marathonId), scheduleId);

        return ScheduleDto.fromSchedule(schedule, withCustomData);
    }

    ///////////
    // v1 stuff

    @Transactional
    public Schedule findByMarathon(final String marathonId) {
        final MarathonEntity marathon = new MarathonEntity();
        marathon.setId(marathonId);

        final Schedule schedule = this.scheduleRepository.findByMarathon(marathon);

        if (schedule != null && schedule.getLines() != null && schedule.getLines().size() > 0) {
            final List<ScheduleLine> lines = schedule.getLines();

            // TODO: i hate this
            lines.get(0).setDate(schedule.getMarathon().getStartDate());

            for (int i = 1; i < lines.size(); i++) {
                final ScheduleLine previous = lines.get(i - 1);
                lines.get(i).setDate(
                    previous.getDate()
                        .plus(previous.getEstimate())
                        .plus(previous.getSetupTime())
                );
            }
        }

        return schedule;
    }

    @Nullable
    public V1ScheduleDto findByMarathonCustomDataControl(final String marathonId, boolean withCustomData) {
        final Schedule byMarathon = this.findByMarathon(marathonId);

        if (byMarathon == null) {
            return null;
        }

        final var schedule = V1ScheduleDto.fromSchedule(byMarathon);

        // Strip custom data instead of adding them :)
        if (!withCustomData) {
            final List<ScheduleLineDto> lines = schedule.getLines();

            // lines can be null so we check if they are not
            if (lines != null && !lines.isEmpty()) {
                // Remove custom data if not requested
                lines.forEach(
                    (line) -> line.setCustomDataDTO(null)
                );
            }
        }

        return schedule;
    }

    @Transactional
    public void deleteByMarathon(final String marathonId) {
        final MarathonEntity marathon = new MarathonEntity();
        marathon.setId(marathonId);
        this.scheduleRepository.deleteByMarathon(marathon);
    }

    public ScheduleTickerDto getForTicker(final String marathonId, boolean withCustomData) throws NotFoundException {
        final V1ScheduleDto schedule = this.findByMarathonCustomDataControl(marathonId, withCustomData);

        if (schedule == null) {
            throw new NotFoundException("Schedule not found");
        }

        final MarathonEntity marathon = this.marathonRepositoryService.findById(marathonId);
        final ZonedDateTime endDate = marathon.getEndDate();
        final ZonedDateTime now = ZonedDateTime.now(endDate.getZone());
        final List<ScheduleLineDto> lines = schedule.getLines();

        if (lines.isEmpty()) {
            throw new EmptyScheduleException("This schedule is empty");
        }

        // fast return if the marathon has ended
        if (now.isAfter(endDate) || now.isEqual(endDate)) {
            return new ScheduleTickerDto().setPrevious(
                lines.get(lines.size() - 1)
            );
        }

        ScheduleLineDto previous = null;
        ScheduleLineDto current = null;
        ScheduleLineDto next = null;

        for (final ScheduleLineDto line : lines) {
            if (now.isEqual(line.getDate()) || now.isAfter(line.getDate())) {
                previous = current;
                current = line;
            } else {
                next = line;
                // we always will find the next one last
                break;
            }
        }

        return new ScheduleTickerDto()
            .setPrevious(previous)
            .setCurrent(current)
            .setNext(next);
    }

    @Transactional
    public void saveOrUpdate(final String marathonId, final Schedule schedule) throws NotFoundException {
        final MarathonEntity marathon =
            this.marathonRepositoryService.findById(marathonId);
        schedule.setMarathon(marathon);
        schedule.getLines().forEach(scheduleLine -> {
            scheduleLine.setSchedule(schedule);

            scheduleLine.getRunners().forEach((runner) -> {
                if (runner.getUser() != null) {
                    runner.setRunnerName(null);
                }
            });
        });
        this.scheduleRepository.save(schedule);
        if (marathon.isScheduleDone()) {
            // TODO: fix
//            this.computeEndDate(marathon, schedule);
            this.marathonRepositoryService.update(marathon);
        }
    }

    public void computeEndDate(final Marathon marathon, final Schedule schedule) {
        marathon.setEndDate(marathon.getStartDate());
        schedule.getLines().forEach(scheduleLine -> {
            marathon.setEndDate(
                marathon.getEndDate().plus(scheduleLine.getEstimate()).plus(scheduleLine.getSetupTime()));
        });
    }
}
