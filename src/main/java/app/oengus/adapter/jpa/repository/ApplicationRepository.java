package app.oengus.adapter.jpa.repository;

import app.oengus.adapter.jpa.entity.ApplicationEntry;
import app.oengus.adapter.jpa.entity.User;
import app.oengus.domain.volunteering.ApplicationStatus;
import app.oengus.entity.model.Team;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface ApplicationRepository extends CrudRepository<ApplicationEntry, Integer> {
    List<ApplicationEntry> findByTeam(Team Team);

    List<ApplicationEntry> findByUserAndStatus(User user, ApplicationStatus status);

    Optional<ApplicationEntry> findByTeamAndUser(Team Team, User user);
}
