package app.oengus.dao;

import app.oengus.entity.model.Marathon;
import app.oengus.entity.model.Team;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamRepository extends CrudRepository<Team, Integer> {
    List<Team> findByMarathon(Marathon marathon);
}
