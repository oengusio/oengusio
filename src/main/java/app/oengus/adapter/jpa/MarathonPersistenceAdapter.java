package app.oengus.adapter.jpa;

import app.oengus.adapter.jpa.entity.MarathonEntity;
import app.oengus.adapter.jpa.entity.User;
import app.oengus.adapter.jpa.mapper.MarathonMapper;
import app.oengus.adapter.jpa.repository.MarathonRepository;
import app.oengus.application.port.persistence.MarathonPersistencePort;
import app.oengus.domain.marathon.Marathon;
import app.oengus.domain.marathon.MarathonStats;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
// TODO: convert methods that just need an id to just accept ids
public class MarathonPersistenceAdapter implements MarathonPersistencePort {
    private final MarathonRepository repository;
    private final MarathonMapper mapper;

    @Override
    public Optional<Marathon> findById(String marathonId) {
        return this.repository.findById(marathonId).map(this.mapper::toDomain);
    }

    @Override
    public Marathon save(Marathon marathon) {
        final var entity = this.mapper.fromDomain(marathon);
        final var savedEntity = this.repository.save(entity);

        return this.mapper.toDomain(savedEntity);
    }

    @Override
    public void delete(Marathon marathon) {
        this.repository.deleteById(marathon.getId());
    }

    @Override
    public boolean existsById(String marathonId) {
        return this.repository.existsById(marathonId);
    }

    @Override
    public List<Marathon> findLive() {
        return this.repository.findLive()
            .stream()
            .map(this.mapper::toDomain)
            .toList();
    }

    @Override
    public List<Marathon> findNextUp() {
        return this.repository.findNext()
            .stream()
            .map(this.mapper::toDomain)
            .toList();
    }

    @Override
    public List<Marathon> findSubmissionsOpen() {
        return this.repository.findBySubmitsOpenTrue()
            .stream()
            .map(this.mapper::toDomain)
            .toList();
    }

    @Override
    public List<Marathon> findActiveModeratedBy(int userId) {
        return this.repository.findActiveMarathonsByCreatorOrModerator(
                User.ofId(userId)
            )
            .stream()
            .map(this.mapper::toDomain)
            .toList();
    }

    @Override
    public List<Marathon> findAllModeratedBy(int userId) {
        return this.repository.findAllMarathonsByCreatorOrModerator(
                User.ofId(userId)
            )
            .stream()
            .map(this.mapper::toDomain)
            .toList();
    }

    @Override
    public List<Marathon> findBetween(ZonedDateTime start, ZonedDateTime end) {
        return this.repository.findBetween(start, end)
            .stream()
            .map(this.mapper::toDomain)
            .toList();
    }

    @Override
    public void markSubmissionsOpen(Marathon marathon) {
        this.repository.openSubmissions(
            MarathonEntity.ofId(marathon.getId())
        );
    }

    @Override
    public void markSubmissionsClosed(Marathon marathon) {
        this.repository.closeSubmissions(
            MarathonEntity.ofId(marathon.getId())
        );
    }

    @Override
    public Optional<MarathonStats> findStatsById(String marathonId) {
        return this.repository.findStats(
            MarathonEntity.ofId(marathonId)
        ).map((rawStats) -> new MarathonStats(
            (Long) rawStats.get("submissionCount"),
            (Long) rawStats.get("runnerCount"),
            (Long) rawStats.get("totalLength"),
            (Double) rawStats.get("averageEstimate")
        ));
    }

    @Override
    public List<Marathon> findNotClearedBefore(ZonedDateTime date) {
        return this.repository.findByClearedFalseAndEndDateBefore(date)
            .stream()
            .map(this.mapper::toDomain)
            .toList();
    }

    @Override
    public void clear(Marathon marathon) {
        this.repository.clearMarathon(
            MarathonEntity.ofId(marathon.getId())
        );
    }

    @Override
    public List<Marathon> findFutureWithScheduledSubmissions() {
        return this.repository.findFutureMarathonsWithScheduledSubmissions()
            .stream()
            .map(this.mapper::toDomain)
            .toList();
    }

    @Override
    public List<Marathon> findAll() {
        return this.repository.findAll()
            .stream()
            .map(this.mapper::toDomain)
            .toList();
    }
}
