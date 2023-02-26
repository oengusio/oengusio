package app.oengus.service.repository;

import app.oengus.dao.GameRepository;
import app.oengus.entity.model.Game;
import javassist.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GameRepositoryService {

    private final GameRepository gameRepository;

    public GameRepositoryService(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    public List<Game> findBySubmissionId(final String marathonId, final int submissionId) {
        return this.gameRepository.findBySubmissionId(marathonId, submissionId);
    }

    public List<Game> findByMarathon(final String marathonId) {
        return this.gameRepository.findByMarathon(marathonId);
    }

    public Game findById(final int id) throws NotFoundException {
        return this.gameRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Game not found"));
    }

    public void delete(final int id) {
        this.gameRepository.deleteById(id);
    }

    public void update(final Game game) {
        this.gameRepository.save(game);
    }

}
