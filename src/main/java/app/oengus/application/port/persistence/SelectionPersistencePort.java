package app.oengus.application.port.persistence;

import app.oengus.domain.submission.Selection;
import app.oengus.entity.model.Status;

import java.util.List;

public interface SelectionPersistencePort {
    List<Selection> findByMarathon(String marathonId);

    List<Selection> findByMarathonWithStatus(String marathonId, List<Status> statuses);

    void rejectTodos(String marathonId);

    void saveAll(List<Selection> selections);
}
