package app.oengus.service.repository;

import app.oengus.adapter.jpa.repository.CategoryRepository;
import app.oengus.entity.model.CategoryEntity;
import javassist.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Deprecated(forRemoval = true)
public class CategoryRepositoryService {

    private final CategoryRepository categoryRepository;

    public CategoryRepositoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<CategoryEntity> findByGameId(String marathonId, int submissionId, int gameId) {
        return this.categoryRepository.findByGameId(marathonId, submissionId, gameId);
    }

    public Iterable<CategoryEntity> findAllById(final List<Integer> ids) {
        return this.categoryRepository.findAllById(ids);
    }

    public CategoryEntity findByCode(final String code) {
        return this.categoryRepository.findByCode(code).orElse(null);
    }

    public CategoryEntity findById(final int id) throws NotFoundException {
        return this.categoryRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Category not found"));
    }

    public void delete(final int id) {
        this.categoryRepository.deleteById(id);
    }

}
