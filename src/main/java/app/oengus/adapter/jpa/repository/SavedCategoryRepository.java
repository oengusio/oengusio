package app.oengus.adapter.jpa.repository;

import app.oengus.adapter.jpa.entity.SavedCategoryEntity;
import app.oengus.adapter.jpa.entity.SavedGameEntity;
import app.oengus.adapter.jpa.entity.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SavedCategoryRepository extends CrudRepository<SavedCategoryEntity, Integer> {
    Optional<SavedCategoryEntity> findByIdAndGame(int id, SavedGameEntity game);

    @Query("SELECT COUNT(c) > 0 FROM SavedCategoryEntity c LEFT JOIN c.game AS g WHERE c = :category AND g.user = :user")
    boolean doesUserOwnCategory(@Param("category") SavedCategoryEntity category, @Param("user") User user);
}
