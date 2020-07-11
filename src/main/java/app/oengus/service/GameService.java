package app.oengus.service;

import app.oengus.entity.dto.GameDto;
import app.oengus.entity.dto.OpponentCategoryDto;
import app.oengus.entity.model.Game;
import app.oengus.helper.BeanHelper;
import app.oengus.service.repository.GameRepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GameService {

	@Autowired
	private GameRepositoryService gameRepositoryService;

	public List<GameDto> findByMarathon(final String marathonId) {
		final List<Game> games = this.gameRepositoryService.findByMarathon(marathonId);
		final List<GameDto> gameDtos = new ArrayList<>();
		if (games != null) {
			games.forEach(g -> {
				final GameDto dto = new GameDto();
				BeanHelper.copyProperties(g, dto, "submission");
				dto.setSubmissionId(g.getSubmission().getId());
				dto.setUser(g.getSubmission().getUser());
				dto.getCategories().forEach(category -> {
					if (category.getOpponents() != null) {
						category.setOpponentDtos(new ArrayList<>());
						category.getOpponents().forEach(opponent -> {
							final OpponentCategoryDto opponentCategoryDto = new OpponentCategoryDto();
							opponentCategoryDto.setId(opponent.getId());
							opponentCategoryDto.setVideo(opponent.getVideo());
							opponentCategoryDto.setUser(opponent.getSubmission().getUser());
							opponentCategoryDto.setAvailabilities(opponent.getSubmission().getAvailabilities());
							category.getOpponentDtos().add(opponentCategoryDto);
						});
					}
				});
				gameDtos.add(dto);
			});
		}
		return gameDtos;
	}

	public void delete(final Integer id) {
		this.gameRepositoryService.delete(id);
	}

}
