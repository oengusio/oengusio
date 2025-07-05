package app.oengus.adapter.jpa.repository;

import app.oengus.adapter.jpa.entity.SavedGameEntity;
import app.oengus.adapter.jpa.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

public interface SavedGameRepository extends CrudRepository<SavedGameEntity, Integer> {
    Page<SavedGameEntity> findByUser(User user, Pageable pageable);
}
