package app.oengus.mock.adapter.jpa;

import app.oengus.application.port.persistence.TeamPersistencePort;
import app.oengus.domain.volunteering.Team;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@Profile("test")
@RequiredArgsConstructor
public class MockTeamPersistenceAdapter implements TeamPersistencePort {
    @Override
    public Optional<Team> findById(int id) {
        return Optional.empty();
    }

    @Override
    public List<Team> findAllForMarathon(String marathonId) {
        return List.of();
    }

    @Override
    public Team save(Team team) {
        return null;
    }
}
