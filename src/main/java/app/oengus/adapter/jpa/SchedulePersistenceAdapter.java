package app.oengus.adapter.jpa;

import app.oengus.adapter.jpa.entity.MarathonEntity;
import app.oengus.adapter.jpa.entity.ScheduleEntity;
import app.oengus.adapter.jpa.mapper.ScheduleEntityMapper;
import app.oengus.adapter.jpa.repository.MarathonRepository;
import app.oengus.adapter.jpa.repository.ScheduleRepository;
import app.oengus.application.port.persistence.SchedulePersistencePort;
import app.oengus.domain.schedule.Line;
import app.oengus.domain.schedule.Schedule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SchedulePersistenceAdapter implements SchedulePersistencePort {
    private final ScheduleRepository repository;
    private final MarathonRepository marathonRepository;
    private final ScheduleEntityMapper mapper;

    @Override
    public Optional<Schedule> findFirstForMarathon(String marathonId) {
        return this.repository.findByMarathonOrderByIdAsc(MarathonEntity.ofId(marathonId))
            .stream()
            .findFirst()
            .map(this::entityToDomain);
    }

    @Override
    public Optional<Schedule> findByIdForMarathon(String marathonId, int scheduleId) {
        return this.repository.findByMarathonAndId(
            MarathonEntity.ofId(marathonId), scheduleId
        )
            .map(this::entityToDomain);
    }

    @Override
    public Optional<Schedule> findByIdForMarathonWithoutLines(String marathonId, int scheduleId) {
        return this.repository.findByMarathonAndId(
                MarathonEntity.ofId(marathonId), scheduleId
            )
            .stream()
            .peek(
                // TODO: will this fuck something up?
                (schedule) -> schedule.setLines(List.of())
            )
            .map(this.mapper::toDomainNoLines)
            .findFirst();
    }

    @Override
    public List<Schedule> findAllForMarathon(String marathonId) {
        return this.repository.findByMarathonOrderByIdAsc(MarathonEntity.ofId(marathonId))
            .stream()
            .map(this::entityToDomain)
            .toList();
    }

    @Override
    public List<Schedule> findAllForMarathonWithoutLines(String marathonId) {
        return this.repository.findByMarathonOrderByIdAsc(MarathonEntity.ofId(marathonId))
            .stream()
            .peek(
                // TODO: will this fuck something up?
                (schedule) -> schedule.setLines(List.of())
            )
            .map(this.mapper::toDomainNoLines)
            .toList();
    }

    @Override
    public Schedule save(Schedule schedule) {
        final var entity = this.mapper.fromDomain(schedule);

        if (entity.getId() < 1) {
            entity.setId(null);
        }

        entity.getLines().forEach((line) -> {
            line.setSchedule(entity);

            if (line.getId() < 1) {
                line.setId(null);
            }
        });

        final var savedEntity = this.repository.save(entity);

        return this.entityToDomain(savedEntity);
    }

    @Override
    public void delete(Schedule schedule) {
        this.repository.deleteById(schedule.getId());
    }

    @Override
    public void deleteAllForMarathon(String marathonId) {
        this.repository.deleteByMarathon(MarathonEntity.ofId(marathonId));
    }

    @Override
    public boolean existsBySlug(String marathonId, String slug) {
        return this.repository.existsByMarathonAndSlug(
            MarathonEntity.ofId(marathonId),
            slug
        );
    }

    private Schedule entityToDomain(ScheduleEntity entity) {
        final var schedule = this.mapper.toDomain(entity);

        // Need to re-fetch the marathon, apparently.
        // TODO: figure out how safe this get is.
        final var marathon = this.marathonRepository.findById(entity.getMarathon().getId()).get();

        this.setDateOnLines(schedule, marathon);

        return schedule;
    }

    private void setDateOnLines(Schedule schedule, MarathonEntity marathon) {
        final var startDate = marathon.getStartDate();
        final var lines = schedule.getLines();

        if (lines.isEmpty()) {
            return;
        }

        lines.get(0).setDate(startDate);

        for (int i = 1; i < lines.size(); i++) {
            final Line previous = lines.get(i - 1);

            lines.get(i).setDate(
                previous.getDate()
                    .plus(previous.getEstimate())
                    .plus(previous.getSetupTime())
            );
        }
    }
}
