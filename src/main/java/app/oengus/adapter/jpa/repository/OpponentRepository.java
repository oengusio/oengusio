package app.oengus.adapter.jpa.repository;

import app.oengus.adapter.jpa.entity.CategoryEntity;
import app.oengus.adapter.jpa.entity.OpponentEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface OpponentRepository extends CrudRepository<OpponentEntity, Integer> {
    void deleteAllByCategory(CategoryEntity category);

    void deleteByCategoryIn(List<CategoryEntity> categories);
}
