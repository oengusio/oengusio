package app.oengus.mock.adapter.jpa;

import app.oengus.application.port.persistence.MarathonPersistencePort;
import app.oengus.domain.OengusUser;
import app.oengus.domain.marathon.Marathon;
import app.oengus.domain.marathon.MarathonStats;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Profile("test")
@Component
@RequiredArgsConstructor
public class MockMarathonPersistenceAdapter implements MarathonPersistencePort {
    private final Map<String, Marathon> fakeDb = new HashMap<>();

    @Override
    public Optional<Marathon> findById(String marathonId) {
        return Optional.ofNullable(this.fakeDb.get(marathonId));
    }

    @Override
    public Optional<OengusUser> findCreatorById(String marathonId) {
        return Optional.empty();
    }

    @Override
    public Marathon save(Marathon marathon) {
        this.fakeDb.put(marathon.getId(), marathon);

        return marathon;
    }

    @Override
    public void delete(Marathon marathon) {
        this.fakeDb.remove(marathon.getId());
    }

    @Override
    public boolean existsById(String marathonId) {
        return this.fakeDb.containsKey(marathonId);
    }

    @Override
    public List<Marathon> findLive() {
        return List.of();
    }

    @Override
    public List<Marathon> findNextUp() {
        return List.of();
    }

    @Override
    public List<Marathon> findSubmissionsOpen() {
        return List.of();
    }

    @Override
    public List<Marathon> findActiveModeratedBy(int userId) {
        return List.of();
    }

    @Override
    public List<Marathon> findAllModeratedBy(int userId) {
        return List.of();
    }

    @Override
    public List<Marathon> findBetween(ZonedDateTime start, ZonedDateTime end) {
        return List.of();
    }

    @Override
    public void markSubmissionsOpen(Marathon marathon) {

    }

    @Override
    public void markSubmissionsClosed(Marathon marathon) {

    }

    @Override
    public Optional<MarathonStats> findStatsById(String marathonId) {
        return Optional.empty();
    }

    @Override
    public List<Marathon> findNotClearedBefore(ZonedDateTime date) {
        return List.of();
    }

    @Override
    public void clear(Marathon marathon) {

    }

    @Override
    public List<Marathon> findFutureWithScheduledSubmissions() {
        return List.of();
    }

    @Override
    public List<Marathon> findAll() {
        return List.of();
    }
}
