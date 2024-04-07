package app.oengus.service.repository;

import app.oengus.dao.SelectionRepository;
import app.oengus.entity.model.CategoryEntity;
import app.oengus.entity.model.Marathon;
import app.oengus.entity.model.Selection;
import app.oengus.entity.model.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SelectionRepositoryService {

	@Autowired
	private SelectionRepository selectionRepository;

	public List<Selection> findByMarathon(final Marathon marathon) {
		return this.selectionRepository.findByMarathon(marathon);
	}

	public List<Selection> findByMarathonAndStatusIn(final Marathon marathon, final List<Status> statuses) {
		return this.selectionRepository.findByMarathonAndStatusIn(marathon, statuses);
	}

	public List<Selection> saveAll(final List<Selection> selections) {
		return (List<Selection>) this.selectionRepository.saveAll(selections);
	}

	public Selection findByCategory(final CategoryEntity category) {
		return this.selectionRepository.findByCategory(category);
	}

	public void rejectTodos(final Marathon marathon) {
		this.selectionRepository.rejectTodos(marathon);
	}

	public List<Selection> findAllByCategory(final List<CategoryEntity> categories) {
		return this.selectionRepository.findAllByCategory(
				categories.stream().map(CategoryEntity::getId).collect(Collectors.toList()));
	}
}
