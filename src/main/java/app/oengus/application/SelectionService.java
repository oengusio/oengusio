package app.oengus.application;

import app.oengus.application.port.persistence.CategoryPersistencePort;
import app.oengus.application.port.persistence.MarathonPersistencePort;
import app.oengus.application.port.persistence.SelectionPersistencePort;
import app.oengus.domain.submission.Selection;
import app.oengus.domain.submission.Status;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SelectionService {
    private final SelectionPersistencePort selectionPersistencePort;
    private final MarathonPersistencePort marathonPersistencePort;
    private final CategoryPersistencePort categoryPersistencePort;

    // TODO: bad name, needs fixing
    public Map<Integer, Selection> findByMarathon(final String marathonId) {
        return this.selectionPersistencePort.findByMarathon(marathonId)
            .stream()
            .collect(Collectors.toMap(Selection::getCategoryId, v -> v));
    }

    public Map<Integer, Selection> findByMarathon(final String marathonId, final List<Status> statuses) {
        if (statuses == null || statuses.isEmpty()) {
            return this.findByMarathon(marathonId);
        }

        return this.selectionPersistencePort.findByMarathonWithStatus(marathonId, statuses)
            .stream()
            .collect(Collectors.toMap(Selection::getCategoryId, v -> v));
    }

    public List<Selection> findAllByMarathonId(final String marathonId) {
        return this.selectionPersistencePort.findByMarathon(marathonId);
    }

    public void saveSelections(String marathonId, final List<Selection> selections) throws NotFoundException {
        if (!this.marathonPersistencePort.existsById(marathonId)) {
            throw new NotFoundException("Marathon not found");
        }

        this.selectionPersistencePort.saveAll(selections);
    }

    public void rejectTodos(final String marathonId) {
        this.selectionPersistencePort.rejectTodos(marathonId);
    }

}
