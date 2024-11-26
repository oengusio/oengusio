package app.oengus.mock.adapter.jpa;

import app.oengus.application.port.persistence.SelectionPersistencePort;
import app.oengus.domain.submission.Selection;
import app.oengus.domain.submission.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Profile("test")
@Component
@RequiredArgsConstructor
public class MockSelectionPersistenceAdapter implements SelectionPersistencePort {
    @Override
    public Optional<Selection> findByCategoryId(int categoryId) {
        return Optional.empty();
    }

    @Override
    public List<Selection> findByCategoryIds(List<Integer> categoryIds) {
        return List.of();
    }

    @Override
    public List<Selection> findByMarathon(String marathonId) {
        return List.of();
    }

    @Override
    public List<Selection> findByMarathonWithStatus(String marathonId, List<Status> statuses) {
        return List.of();
    }

    @Override
    public void rejectTodos(String marathonId) {

    }

    @Override
    public void saveAll(List<Selection> selections) {

    }
}
