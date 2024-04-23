package app.oengus.application;

import app.oengus.application.port.persistence.TeamPersistencePort;
import app.oengus.domain.volunteering.Team;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TeamService {
    private final TeamPersistencePort teamPersistencePort;

    public Optional<Team> findById(final int teamId) {
        return this.teamPersistencePort.findById(teamId);
    }

    public List<Team> getAll(final String marathonId) {
        return this.teamPersistencePort.findAllForMarathon(marathonId);
    }

    public Team save(final Team team) {
        return this.teamPersistencePort.save(team);
    }
}
