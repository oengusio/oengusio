package app.oengus.adapter.jpa.repository;

import app.oengus.adapter.jpa.entity.CategoryEntity;
import app.oengus.adapter.jpa.entity.OpponentEntity;
import app.oengus.adapter.jpa.entity.SubmissionEntity;
import app.oengus.adapter.jpa.entity.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OpponentRepository extends CrudRepository<OpponentEntity, Integer> {
    void deleteAllByCategory(CategoryEntity category);

    void deleteByCategoryIn(List<CategoryEntity> categories);

    List<OpponentEntity> findBySubmissionUser(User user);

    // This only returns the submission WITH the games that we joined
    // Ignore how cursed this query is please
    @Query(value =
        "SELECT s FROM OpponentEntity o " +
            "INNER JOIN o.category c ON o.category = c " +
            "INNER JOIN c.game g ON c.game = g " +
            "INNER JOIN g.submission s ON g.submission = s " +
            "JOIN FETCH s.games sg " +
            "INNER JOIN sg.categories sgc ON o.category = sgc " +
            "WHERE o.submission.user = :user ")
    List<SubmissionEntity> findWhereUserIsOpponent(@Param("user") User user);
}
