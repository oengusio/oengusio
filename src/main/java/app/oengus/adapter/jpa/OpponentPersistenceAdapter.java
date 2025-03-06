package app.oengus.adapter.jpa;

import app.oengus.adapter.jpa.entity.User;
import app.oengus.adapter.jpa.mapper.OpponentMapper;
import app.oengus.adapter.jpa.mapper.SubmissionEntityMapper;
import app.oengus.adapter.jpa.repository.OpponentRepository;
import app.oengus.application.port.persistence.OpponentPersistencePort;
import app.oengus.domain.submission.Opponent;
import app.oengus.domain.submission.Submission;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Profile("!test")
@RequiredArgsConstructor
public class OpponentPersistenceAdapter implements OpponentPersistencePort {
    private final OpponentMapper mapper;
    private final SubmissionEntityMapper submissionMapper;
    private final OpponentRepository opponentRepository;

    @Override
    public List<Opponent> findByUser(int userId) {
        return this.opponentRepository.findBySubmissionUser(User.ofId(userId))
            .stream()
            .map(this.mapper::toDomain)
            .toList();
    }

    @Override
    public List<Submission> findParentSubmissionsForUser(int userId) {
        return this.opponentRepository.findWhereUserIsOpponent(User.ofId(userId))
            .stream()
            .map(this.submissionMapper::toDomain)
            .toList();
    }
}
