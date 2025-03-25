package app.oengus.adapter.jpa;

import app.oengus.adapter.jpa.entity.MarathonEntity;
import app.oengus.adapter.jpa.mapper.TeamMapper;
import app.oengus.adapter.jpa.repository.TeamRepository;
import app.oengus.application.port.persistence.TeamPersistencePort;
import app.oengus.domain.volunteering.Team;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TeamPersistenceAdapter implements TeamPersistencePort {
    private final TeamRepository repository;
    private final TeamMapper mapper;

    @Override
    public Optional<Team> findById(int id) {
        return this.repository.findById(id)
            .map(this.mapper::toDomain);
    }

    @Override
    public List<Team> findAllForMarathon(String marathonId) {
        return this.repository.findByMarathon(
                MarathonEntity.ofId(marathonId)
            )
            .stream()
            .map(this.mapper::toDomain)
            .toList();
    }

    @Override
    public Team save(Team team) {
        final var entity = this.mapper.fromDomain(team);

        if (entity.getId() < 1) {
            entity.setId(null);
        }

        final var savedEntity = this.repository.save(entity);

        return this.mapper.toDomain(savedEntity);
    }
}
