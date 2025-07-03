package app.oengus.adapter.jpa.repository;

import app.oengus.adapter.jpa.entity.SavedCategoryEntity;
import org.springframework.data.repository.CrudRepository;

public interface SavedCategoryRepository extends CrudRepository<SavedCategoryEntity, Integer> {
}
