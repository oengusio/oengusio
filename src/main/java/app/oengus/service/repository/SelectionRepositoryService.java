package app.oengus.service.repository;

import app.oengus.dao.SelectionRepository;
import app.oengus.entity.model.CategoryEntity;
import app.oengus.adapter.jpa.entity.MarathonEntity;
import app.oengus.entity.model.SelectionEntity;
import app.oengus.entity.model.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Deprecated(forRemoval = true)
public class SelectionRepositoryService {

	@Autowired
	private SelectionRepository selectionRepository;

	public List<SelectionEntity> findByMarathon(final MarathonEntity marathon) {
		return this.selectionRepository.findByMarathon(marathon);
	}

	public List<SelectionEntity> findByMarathonAndStatusIn(final MarathonEntity marathon, final List<Status> statuses) {
		return this.selectionRepository.findByMarathonAndStatusIn(marathon, statuses);
	}

	public List<SelectionEntity> saveAll(final List<SelectionEntity> selections) {
		return (List<SelectionEntity>) this.selectionRepository.saveAll(selections);
	}

	public SelectionEntity findByCategory(final CategoryEntity category) {
		return this.selectionRepository.findByCategory(category);
	}

	public void rejectTodos(final MarathonEntity marathon) {
		this.selectionRepository.rejectTodos(marathon);
	}

	public List<SelectionEntity> findAllByCategory(final List<CategoryEntity> categories) {
		return this.selectionRepository.findAllByCategory(
				categories.stream().map(CategoryEntity::getId).collect(Collectors.toList()));
	}
}
