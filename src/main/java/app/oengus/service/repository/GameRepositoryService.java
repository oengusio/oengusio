package app.oengus.service.repository;

import app.oengus.dao.GameRepository;
import app.oengus.entity.model.Game;
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

	public void delete(final Integer id) {
		this.gameRepository.deleteById(id);
	}

}
