package app.oengus.dao;

import app.oengus.entity.constants.PatreonPledgeStatus;
import app.oengus.entity.model.PatreonStatus;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PatreonStatusRepository extends CrudRepository<PatreonStatus, String> {
    List<PatreonStatus> findByStatus(PatreonPledgeStatus status);
}
