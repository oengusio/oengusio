package app.oengus.adapter.jpa.repository;

import app.oengus.adapter.jpa.entity.MarathonEntity;
import app.oengus.entity.model.TeamEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TeamRepository extends CrudRepository<TeamEntity, Integer> {
    List<TeamEntity> findByMarathon(MarathonEntity marathon);
}
