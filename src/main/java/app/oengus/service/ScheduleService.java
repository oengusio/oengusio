package app.oengus.service;

import app.oengus.entity.dto.ScheduleTickerDto;
import app.oengus.entity.dto.v2.schedule.ScheduleDto;
import app.oengus.entity.model.Marathon;
import app.oengus.entity.model.Schedule;
import app.oengus.entity.model.ScheduleLine;
import app.oengus.exception.schedule.EmptyScheduleException;
import app.oengus.service.repository.MarathonRepositoryService;
import app.oengus.service.repository.ScheduleRepositoryService;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.ZonedDateTime;
import java.util.List;

@Service
public class ScheduleService {
    private final ScheduleRepositoryService scheduleRepository;
    private final MarathonRepositoryService marathonRepositoryService;

    @Autowired
    public ScheduleService(ScheduleRepositoryService scheduleRepository, MarathonRepositoryService marathonRepositoryService) {
        this.scheduleRepository = scheduleRepository;
        this.marathonRepositoryService = marathonRepositoryService;
    }

    ///////////
    // v2 stuff

    public List<ScheduleDto> findAllByMarathon(final String marathonId, boolean withCustomData) {
        return this.scheduleRepository.findAllByMarathon(Marathon.ofId(marathonId))
            .stream()
            .map((schedule) -> ScheduleDto.fromSchedule(schedule, withCustomData))
            .toList();
    }

    public ScheduleDto findByScheduleId(final String marathonId, final int scheduleId, boolean withCustomData) throws NotFoundException {
        final Schedule schedule = this.scheduleRepository.findById(Marathon.ofId(marathonId), scheduleId);

        return ScheduleDto.fromSchedule(schedule, withCustomData);
    }

    ///////////
    // v1 stuff

    @Transactional
    public Schedule findByMarathon(final String marathonId) {
        final Marathon marathon = new Marathon();
        marathon.setId(marathonId);

        final Schedule schedule = this.scheduleRepository.findByMarathon(marathon);

        if (schedule != null && schedule.getLines() != null && schedule.getLines().size() > 0) {
            final List<ScheduleLine> lines = schedule.getLines();

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

    public Schedule findByMarathonCustomDataControl(final String marathonId, boolean withCustomData) {
        final Schedule byMarathon = this.findByMarathon(marathonId);

        if (withCustomData && byMarathon != null) {
            final List<ScheduleLine> lines = byMarathon.getLines();

            // lines can be null so we check if they are not
            if (lines != null && !lines.isEmpty()) {
                // Make the custom data public if requested
                lines.forEach(
                    (line) -> line.setCustomDataDTO(line.getCustomData())
                );
            }
        }

        return byMarathon;
    }

    @Transactional
    public void deleteByMarathon(final String marathonId) {
        final Marathon marathon = new Marathon();
        marathon.setId(marathonId);
        this.scheduleRepository.deleteByMarathon(marathon);
    }

    public ScheduleTickerDto getForTicker(final String marathonId, boolean withCustomData) throws NotFoundException {
        final Schedule schedule = this.findByMarathonCustomDataControl(marathonId, withCustomData);

        if (schedule == null) {
            throw new NotFoundException("Schedule not found");
        }

        final ZonedDateTime endDate = schedule.getMarathon().getEndDate();
        final ZonedDateTime now = ZonedDateTime.now(endDate.getZone());
        final List<ScheduleLine> lines = schedule.getLines();

        if (lines.isEmpty()) {
            throw new EmptyScheduleException("This schedule is empty");
        }

        // fast return if the marathon has ended
        if (now.isAfter(endDate) || now.isEqual(endDate)) {
            return new ScheduleTickerDto().setPrevious(
                lines.get(lines.size() - 1)
            );
        }

        ScheduleLine previous = null;
        ScheduleLine current = null;
        ScheduleLine next = null;

        for (final ScheduleLine line : lines) {
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
        final Marathon marathon =
            this.marathonRepositoryService.findById(marathonId);
        schedule.setMarathon(marathon);
        schedule.getLines().forEach(scheduleLine -> scheduleLine.setSchedule(schedule));
        this.scheduleRepository.save(schedule);
        if (marathon.isScheduleDone()) {
            this.computeEndDate(marathon, schedule);
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
