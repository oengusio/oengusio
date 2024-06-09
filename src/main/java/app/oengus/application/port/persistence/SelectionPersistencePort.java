package app.oengus.application.port.persistence;

import app.oengus.domain.submission.Selection;
import app.oengus.domain.submission.Status;

import java.util.List;
import java.util.Optional;

public interface SelectionPersistencePort {
    Optional<Selection> findByCategoryId(int categoryId);

    List<Selection> findByCategoryIds(List<Integer> categoryIds);

    List<Selection> findByMarathon(String marathonId);

    List<Selection> findByMarathonWithStatus(String marathonId, List<Status> statuses);

    void rejectTodos(String marathonId);

    void saveAll(List<Selection> selections);
}
