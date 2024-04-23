package app.oengus.application.port.persistence;

import app.oengus.domain.volunteering.Team;

import java.util.List;
import java.util.Optional;

public interface TeamPersistencePort {
    Optional<Team> findById(int id);

    List<Team> findAllForMarathon(String marathonId);

    Team save(Team team);
}
