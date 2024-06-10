package app.oengus.adapter.jpa;

import app.oengus.adapter.jpa.entity.*;
import app.oengus.adapter.jpa.mapper.SubmissionEntityMapper;
import app.oengus.adapter.jpa.mapper.UserMapper;
import app.oengus.adapter.jpa.repository.SubmissionRepository;
import app.oengus.application.port.persistence.SubmissionPersistencePort;
import app.oengus.domain.OengusUser;
import app.oengus.domain.submission.Status;
import app.oengus.domain.submission.Submission;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SubmissionPersistenceAdapter implements SubmissionPersistencePort {
    private final SubmissionRepository repository;
    private final SubmissionEntityMapper mapper;
    private final UserMapper userMapper;

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
            MarathonEntity.ofId(marathonId)
        ).map(this.mapper::toDomain);
    }

    @Override
    public boolean existsForUserInMarathon(int userId, String marathonId) {
        return this.repository.existsByMarathonAndUser(
            MarathonEntity.ofId(marathonId),
            User.ofId(userId)
        );
    }

    @Override
    public List<Submission> findAcceptedInMarathon(String marathonId) {
        return this.repository.findValidatedOrBonusSubmissionsForMarathon(
            MarathonEntity.ofId(marathonId)
        )
            .stream()
            .map(this.mapper::toDomain)
            .toList();
    }

    @Override
    public Page<Submission> searchInMarathon(String marathonId, String query, Pageable pageable) {
        return this.repository.searchForMarathon(
            MarathonEntity.ofId(marathonId),
            query,
            pageable
        ).map(this.mapper::toDomain);
    }

    @Override
    public Page<Submission> searchInMarathon(String marathonId, String query, Status status, Pageable pageable) {
        return this.repository.searchForMarathonWithStatus(
            MarathonEntity.ofId(marathonId),
            query,
            status,
            pageable
        ).map(this.mapper::toDomain);
    }

    @Override
    public Submission save(Submission submission) {
        final var rawEntity = this.mapper.fromDomain(submission);

        if (rawEntity.getId() < 1) {
            rawEntity.setId(null);
        }

        rawEntity.getAnswers().forEach((answer) -> {
            answer.setSubmission(rawEntity);

            if (answer.getId() < 1) {
                answer.setId(null);
            }
        });

        rawEntity.getOpponents().forEach((opponent) -> {
            opponent.setSubmission(rawEntity);

            if (opponent.getId() < 1) {
                opponent.setId(null);
            }
        });

        rawEntity.getGames().forEach((game) -> {
            game.setSubmission(rawEntity);

            if (game.getId() < 1) {
                game.setId(null);
            }

            game.getCategories().forEach((category) -> {
                if (category.getId() < 1) {
                    category.setId(null);
                }

                category.setGame(game);
            });
        });

        final var savedEntity = this.repository.save(rawEntity);

        return this.mapper.toDomain(savedEntity);
    }

    @Override
    public void deleteByMarathon(String marathonId) {
        this.repository.deleteByMarathon(MarathonEntity.ofId(marathonId));
    }

    @Override
    public void delete(Submission submission) {
        this.repository.deleteById(submission.getId());
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
            MarathonEntity.ofId(marathonId),
            pageable
        ).map(this.mapper::toDomain);
    }

    @Override
    public List<Submission> findAllByMarathon(String marathonId) {
        return this.repository.findByMarathonOrderByIdAsc(
            MarathonEntity.ofId(marathonId)
        )
            .stream()
            .map(this.mapper::toDomain)
            .toList();
    }

    @Override
    public Optional<Integer> getUserIdFromOpponentId(int opponentId) {
        return this.repository.findFirstByOpponentsContaining(OpponentEntity.ofId(opponentId))
            .map((it) -> it.getUser().getId());
    }

    @Override
    public List<OengusUser> findUsersByIds(List<Integer> submissionIds) {
        return ((List<SubmissionEntity>) this.repository.findAllById(submissionIds))
            .stream()
            .map(SubmissionEntity::getUser)
            .map(this.userMapper::toDomain)
            .toList();
    }
}
