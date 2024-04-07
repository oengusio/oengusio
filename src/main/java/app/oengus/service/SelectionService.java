package app.oengus.service;

import app.oengus.entity.dto.SelectionDto;
import app.oengus.entity.model.CategoryEntity;
import app.oengus.entity.model.Marathon;
import app.oengus.entity.model.Selection;
import app.oengus.entity.model.Status;
import app.oengus.service.repository.CategoryRepositoryService;
import app.oengus.service.repository.MarathonRepositoryService;
import app.oengus.service.repository.SelectionRepositoryService;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SelectionService {

    @Autowired
    private SelectionRepositoryService selectionRepository;

    @Autowired
    private CategoryRepositoryService categoryRepositoryService;

    @Autowired
    private MarathonRepositoryService marathonRepositoryService;

    public Map<Integer, SelectionDto> findByMarathon(final String marathonId) {
        final Marathon marathon = new Marathon();
        marathon.setId(marathonId);

        final List<Selection> selections = this.selectionRepository.findByMarathon(marathon);

        return this.modelToDtos(selections);
    }

    public List<Selection> findByMarathon(final Marathon marathon) {
        return this.selectionRepository.findByMarathon(marathon);
    }

    public Map<Integer, SelectionDto> findByMarathon(final String marathonId, final List<Status> statuses) {
        final Marathon marathon = new Marathon();
        marathon.setId(marathonId);
        if (statuses == null || statuses.isEmpty()) {
            return this.findByMarathon(marathonId);
        } else {
            return this.modelToDtos(this.selectionRepository.findByMarathonAndStatusIn(marathon, statuses));
        }
    }

    public Map<Integer, SelectionDto> findAllByCategory(final List<CategoryEntity> categories) {
        final List<Selection> selections = this.selectionRepository.findAllByCategory(categories);
        return this.modelToDtos(selections);
    }

    private Map<Integer, SelectionDto> modelToDtos(final List<Selection> selections) {
        final Map<Integer, SelectionDto> dtos = new HashMap<>();
        selections.forEach(selection -> {
            final SelectionDto dto = new SelectionDto();
            dto.setId(selection.getId());
            dto.setCategoryId(selection.getCategory().getId());
            dto.setStatus(selection.getStatus());
            dtos.put(selection.getCategory().getId(), dto);
        });

        return dtos;
    }

    @Transactional
    public void saveOrUpdate(final String marathonId, final List<SelectionDto> dtos) throws NotFoundException {
        final Marathon marathon = this.marathonRepositoryService.findById(marathonId);
        final List<Selection> newSelections = new ArrayList<>();
        final Iterable<CategoryEntity> categories = this.categoryRepositoryService.findAllById(
            dtos.stream().map(SelectionDto::getCategoryId).collect(Collectors.toList())
        );

        final Map<Integer, CategoryEntity> categoryMap = new HashMap<>();
        categories.forEach((category) -> categoryMap.put(category.getId(), category));

        dtos.forEach((dto) -> {
            final CategoryEntity category = categoryMap.get(dto.getCategoryId());

            if (category != null) {
                final Selection selection = new Selection();
                selection.setId(dto.getId());
                selection.setCategory(category);
                selection.setMarathon(marathon);
                selection.setStatus(dto.getStatus());
                newSelections.add(selection);
            }
        });

        this.selectionRepository.saveAll(newSelections);

    }

    @Transactional
    public void rejectTodos(final Marathon marathon) {
        this.selectionRepository.rejectTodos(marathon);
    }

}
