package app.oengus.adapter.jpa;

import app.oengus.adapter.jpa.entity.User;
import app.oengus.adapter.jpa.mapper.SubmissionEntityMapper;
import app.oengus.adapter.jpa.repository.SubmissionRepository;
import app.oengus.application.port.persistence.SubmissionPersistencePort;
import app.oengus.domain.Submission;
import app.oengus.entity.model.GameEntity;
import app.oengus.entity.model.Marathon;
import app.oengus.entity.model.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SubmissionPersistenceAdapter implements SubmissionPersistencePort {
    private final SubmissionRepository repository;
    private final SubmissionEntityMapper mapper;

    @Override
    public Optional<Submission> findById(int id) {
        return this.repository.findById(id).map(this.mapper::toDomain);
    }

    @Override
    public Submission getByGameId(int gameId) {
        final var entity = this.repository.findByGamesContaining(
            GameEntity.ofId(gameId)
        );

        return this.mapper.toDomain(entity);
    }

    @Override
    public Optional<Submission> findForUserInMarathon(int userId, String marathonId) {
        return this.repository.findByUserAndMarathon(
            User.ofId(userId),
            Marathon.ofId(marathonId)
        ).map(this.mapper::toDomain);
    }

    @Override
    public List<Submission> findAcceptedInMarathon(String marathonId) {
        return this.repository.findValidatedOrBonusSubmissionsForMarathon(
            Marathon.ofId(marathonId)
        )
            .stream()
            .map(this.mapper::toDomain)
            .toList();
    }

    @Override
    public Page<Submission> searchInMarathon(String marathonId, String query, Pageable pageable) {
        return this.repository.searchForMarathon(
            Marathon.ofId(marathonId),
            query,
            pageable
        ).map(this.mapper::toDomain);
    }

    @Override
    public Page<Submission> searchInMarathon(String marathonId, String query, Status status, Pageable pageable) {
        return this.repository.searchForMarathonWithStatus(
            Marathon.ofId(marathonId),
            query,
            status,
            pageable
        ).map(this.mapper::toDomain);
    }

    @Override
    public Submission save(Submission submission) {
        final var rawEntity = this.mapper.fromDomain(submission);
        final var savedEntity = this.repository.save(rawEntity);

        return this.mapper.toDomain(savedEntity);
    }

    @Override
    public void deleteByMarathon(String marathonId) {
        this.repository.deleteByMarathon(Marathon.ofId(marathonId));
    }

    @Override
    public List<Submission> findByUser(int userId) {
        return this.repository.findByUser(User.ofId(userId))
            .stream()
            .map(this.mapper::toDomain)
            .toList();
    }

    @Override
    public Page<Submission> findByMarathon(String marathonId, Pageable pageable) {
        return this.repository.findByMarathonOrderByIdAsc(
            Marathon.ofId(marathonId),
            pageable
        ).map(this.mapper::toDomain);
    }

    @Override
    public List<Submission> findAllByMarathon(String marathonId) {
        return this.repository.findByMarathonOrderByIdAsc(
            Marathon.ofId(marathonId)
        )
            .stream()
            .map(this.mapper::toDomain)
            .toList();
    }
}
