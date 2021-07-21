package app.oengus.dao;

import app.oengus.entity.model.Application;
import app.oengus.entity.model.Team;
import app.oengus.entity.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends CrudRepository<Application, Integer> {
    List<Application> findByTeam(Team Team);

    Optional<Application> findByTeamAndUser(Team Team, User user);
}
