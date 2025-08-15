package app.oengus.adapter.jpa.repository;

import app.oengus.adapter.jpa.entity.SavedGameEntity;
import app.oengus.adapter.jpa.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface SavedGameRepository extends CrudRepository<SavedGameEntity, Integer> {
    Optional<SavedGameEntity> findByIdAndUser(int id, User user);

    Page<SavedGameEntity> findByUser(User user, Pageable pageable);
}
