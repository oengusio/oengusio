package app.oengus.adapter.jpa;

import app.oengus.adapter.jpa.entity.MarathonEntity;
import app.oengus.adapter.jpa.entity.User;
import app.oengus.adapter.jpa.mapper.MarathonMapper;
import app.oengus.adapter.jpa.mapper.UserMapper;
import app.oengus.adapter.jpa.repository.MarathonRepository;
import app.oengus.application.port.persistence.MarathonPersistencePort;
import app.oengus.domain.OengusUser;
import app.oengus.domain.marathon.Marathon;
import app.oengus.domain.marathon.MarathonStats;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
// TODO: convert methods that just need an id to just accept ids
public class MarathonPersistenceAdapter implements MarathonPersistencePort {
    private final MarathonRepository repository;
    private final MarathonMapper mapper;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public Optional<Marathon> findById(String marathonId) {
        return this.repository.findById(marathonId).map(this.mapper::toDomain);
    }

    @Override
    @Transactional
    public Optional<OengusUser> findCreatorById(String marathonId) {
        return this.repository.findCreatorById(MarathonEntity.ofId(marathonId))
            .map(this.userMapper::toDomain);
    }

    @Override
    @Transactional
    public Marathon save(Marathon marathon) {
        final var entity = this.mapper.fromDomain(marathon);

        entity.getQuestions().forEach((question) -> {
            question.setMarathon(entity);

            if (question.getId() < 1) {
                question.setId(null);
            }
        });

        // HACK: teams are not stored in the current domain model so we need to do it this way.
        entity.setTeams(List.of());

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
    @Transactional
    public List<Marathon> findLive() {
        return this.repository.findLive()
            .stream()
            .map(this.mapper::toDomain)
            .toList();
    }

    @Override
    @Transactional
    public List<Marathon> findNextUp() {
        return this.repository.findNext()
            .stream()
            .map(this.mapper::toDomain)
            .toList();
    }

    @Override
    @Transactional
    public List<Marathon> findSubmissionsOpen() {
        return this.repository.findBySubmitsOpenTrue()
            .stream()
            .map(this.mapper::toDomain)
            .toList();
    }

    @Override
    @Transactional
    public List<Marathon> findActiveModeratedBy(int userId) {
        return this.repository.findActiveMarathonsByCreatorOrModerator(
                User.ofId(userId)
            )
            .stream()
            .map(this.mapper::toDomain)
            .toList();
    }

    @Override
    @Transactional
    public List<Marathon> findAllModeratedBy(int userId) {
        return this.repository.findAllMarathonsByCreatorOrModerator(
                User.ofId(userId)
            )
            .stream()
            .map(this.mapper::toDomain)
            .toList();
    }

    @Override
    @Transactional
    public List<Marathon> findBetween(ZonedDateTime start, ZonedDateTime end) {
        return this.repository.findBetween(start, end)
            .stream()
            .map(this.mapper::toDomain)
            .toList();
    }

    @Override
    @Transactional
    public void markSubmissionsOpen(Marathon marathon) {
        this.repository.openSubmissions(
            MarathonEntity.ofId(marathon.getId())
        );
    }

    @Override
    @Transactional
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
            safeBigDecToLong(rawStats.get("totalLength")), // Why is this a big dec? I don't know
            safeObjectToDouble(rawStats.get("averageEstimate"))
        ));
    }

    @Override
    @Transactional
    public List<Marathon> findNotClearedBefore(ZonedDateTime date) {
        return this.repository.findByClearedFalseAndEndDateBefore(date)
            .stream()
            .map(this.mapper::toDomain)
            .toList();
    }

    @Override
    @Transactional
    public void clear(Marathon marathon) {
        this.repository.clearMarathon(
            MarathonEntity.ofId(marathon.getId())
        );
    }

    @Override
    @Transactional // somehow, this makes stuff not crash WTF
    public List<Marathon> findFutureWithScheduledSubmissions() {
        return this.repository.findFutureMarathonsWithScheduledSubmissions()
            .stream()
            .map(this.mapper::toDomain)
            .toList();
    }

    @Override
    @Transactional
    public List<Marathon> findAll() {
        return this.repository.findAll()
            .stream()
            .map(this.mapper::toDomain)
            .toList();
    }

    // TODO: possibly extract these to helper functions in the domain model
    private long safeBigDecToLong(Object rawVal) {
        if (rawVal instanceof BigDecimal bigDec) {
            return bigDec.longValue();
        }

        return 0L;
    }

    private double safeObjectToDouble(Object rawVal) {
        if (rawVal instanceof Double doubleVal) {
            return doubleVal;
        }

        return 0d;
    }
}
