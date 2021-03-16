package app.oengus.service.repository;

import app.oengus.dao.CategoryRepository;
import app.oengus.entity.model.Category;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryRepositoryService {

    @Autowired
    private CategoryRepository categoryRepository;

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
