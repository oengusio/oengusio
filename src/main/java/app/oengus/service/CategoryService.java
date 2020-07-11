package app.oengus.service;

import app.oengus.entity.dto.OpponentSubmissionDto;
import app.oengus.entity.model.Category;
import app.oengus.entity.model.RunType;
import app.oengus.entity.model.User;
import app.oengus.exception.OengusBusinessException;
import app.oengus.helper.PrincipalHelper;
import app.oengus.service.repository.CategoryRepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class CategoryService {

	@Autowired
	private CategoryRepositoryService categoryRepositoryService;

	private static final List<RunType> MULTIPLAYER_RUN_TYPES = List.of(RunType.COOP_RACE, RunType.COOP, RunType.RACE);

	public OpponentSubmissionDto findCategoryByCode(final String marathonId, final String code) {
		final Category category = this.categoryRepositoryService.findByCode(code);
		final User user = PrincipalHelper.getCurrentUser();
		if (category != null) {
			if (!Objects.equals(category.getGame().getSubmission().getMarathon().getId(),
					marathonId)) {
				throw new OengusBusinessException("DIFFERENT_MARATHON");
			}
			if (!MULTIPLAYER_RUN_TYPES.contains(category.getType())) {
				throw new OengusBusinessException("NOT_MULTIPLAYER");
			}
			if (user != null) {
				if (Objects.equals(category.getGame().getSubmission().getUser().getId(), user.getId())) {
					throw new OengusBusinessException("SAME_USER");
				}
				if (category.getOpponents()
				            .stream()
				            .map(opponent -> opponent.getSubmission().getUser())
				            .anyMatch(user1 -> user1.getId().equals(user.getId()))) {
					throw new OengusBusinessException("ALREADY_IN_OPPONENTS");
				}
			}
			final OpponentSubmissionDto opponentDto = new OpponentSubmissionDto();
			final List<User> users = new ArrayList<>();
			users.add(category.getGame().getSubmission().getUser());
			users.addAll(category
					.getOpponents()
					.stream()
					.map(opponent -> opponent.getSubmission().getUser())
					.collect(
							Collectors.toSet()));
			if (category.getGame().getSubmission().getMarathon().getMaxNumberOfScreens() <= users.size()) {
				throw new OengusBusinessException("MAX_SIZE_REACHED");
			}
			opponentDto.setUsers(users);
			opponentDto.setGameName(category.getGame().getName());
			opponentDto.setCategoryName(category.getName());
			opponentDto.setCategoryId(category.getId());
			return opponentDto;
		}
		throw new OengusBusinessException("CODE_NOT_FOUND");
	}

	public void delete(final Integer id) {
		this.categoryRepositoryService.delete(id);
	}

}
