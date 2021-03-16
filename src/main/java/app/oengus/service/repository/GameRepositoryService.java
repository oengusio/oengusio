package app.oengus.service.repository;

import app.oengus.dao.GameRepository;
import app.oengus.entity.model.Game;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GameRepositoryService {

    @Autowired
    private GameRepository gameRepository;

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

}
