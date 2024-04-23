package app.oengus.adapter.jpa;

import app.oengus.adapter.jpa.entity.MarathonEntity;
import app.oengus.adapter.jpa.mapper.SelectionMapper;
import app.oengus.adapter.jpa.repository.SelectionRepository;
import app.oengus.application.port.persistence.SelectionPersistencePort;
import app.oengus.domain.submission.Selection;
import app.oengus.domain.submission.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SelectionPersistenceAdapter implements SelectionPersistencePort {
    private final SelectionRepository repository;
    private final SelectionMapper mapper;

    @Override
    public List<Selection> findByMarathon(String marathonId) {
        return this.repository.findByMarathon(
            MarathonEntity.ofId(marathonId)
        ).stream().map(this.mapper::toDomain).toList();
    }

    @Override
    public List<Selection> findByMarathonWithStatus(String marathonId, List<Status> statuses) {
        return this.repository.findByMarathonAndStatusIn(
            MarathonEntity.ofId(marathonId),
            statuses
        ).stream().map(this.mapper::toDomain).toList();
    }

    @Transactional
    @Override
    public void rejectTodos(String marathonId) {
        this.repository.rejectTodos(MarathonEntity.ofId(marathonId));
    }

    @Override
    public void saveAll(List<Selection> selections) {
        final var entities = selections.stream().map(this.mapper::fromDomain).toList();

        this.repository.saveAll(entities);
    }
}
