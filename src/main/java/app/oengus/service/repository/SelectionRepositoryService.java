package app.oengus.service.repository;

import app.oengus.dao.SelectionRepository;
import app.oengus.entity.model.Category;
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

	public void saveAll(final List<Selection> selections, final String marathonId) {
		this.selectionRepository.saveAll(selections);
	}

	public Selection findByCategory(final Category category) {
		return this.selectionRepository.findByCategory(category);
	}

	public void rejectTodos(final Marathon marathon) {
		this.selectionRepository.rejectTodos(marathon);
	}

	public List<Selection> findAllByCategory(final List<Category> categories) {
		return this.selectionRepository.findAllByCategory(
				categories.stream().map(Category::getId).collect(Collectors.toList()));
	}
}
