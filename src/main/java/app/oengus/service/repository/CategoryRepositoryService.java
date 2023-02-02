package app.oengus.service.repository;

import app.oengus.dao.CategoryRepository;
import app.oengus.entity.model.Category;
import javassist.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryRepositoryService {

    private final CategoryRepository categoryRepository;

    public CategoryRepositoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> findByGameId(String marathonId, int submissionId, int gameId) {
        return this.categoryRepository.findByGameId(marathonId, submissionId, gameId);
    }

    public Iterable<Category> findAllById(final List<Integer> ids) {
        return this.categoryRepository.findAllById(ids);
    }

    public Category findByCode(final String code) {
        return this.categoryRepository.findByCode(code);
    }

    public Category findById(final int id) throws NotFoundException {
        return this.categoryRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Category not found"));
    }

    public void delete(final int id) {
        this.categoryRepository.deleteById(id);
    }

}
