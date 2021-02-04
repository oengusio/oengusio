package app.oengus.service;

import app.oengus.service.repository.GameRepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GameService {

	@Autowired
	private GameRepositoryService gameRepositoryService;

	public void delete(final Integer id) {
		this.gameRepositoryService.delete(id);
	}

}
