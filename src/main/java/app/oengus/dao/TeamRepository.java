package app.oengus.dao;

import app.oengus.entity.model.Marathon;
import app.oengus.entity.model.Team;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TeamRepository extends CrudRepository<Team, Integer> {
    List<Team> findByMarathon(Marathon marathon);
}
