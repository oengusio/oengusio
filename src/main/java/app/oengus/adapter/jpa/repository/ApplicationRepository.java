package app.oengus.adapter.jpa.repository;

import app.oengus.domain.volunteering.ApplicationStatus;
import app.oengus.adapter.jpa.entity.ApplicationEntry;
import app.oengus.entity.model.Team;
import app.oengus.adapter.jpa.entity.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends CrudRepository<ApplicationEntry, Integer> {
    List<ApplicationEntry> findByTeam(Team Team);

    List<ApplicationEntry> findByUserAndStatus(User user, ApplicationStatus status);

    Optional<ApplicationEntry> findByTeamAndUser(Team Team, User user);
}
