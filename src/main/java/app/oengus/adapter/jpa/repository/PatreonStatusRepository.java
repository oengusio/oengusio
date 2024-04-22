package app.oengus.adapter.jpa.repository;

import app.oengus.domain.PatreonPledgeStatus;
import app.oengus.adapter.jpa.entity.PatreonStatus;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PatreonStatusRepository extends CrudRepository<PatreonStatus, String> {
    List<PatreonStatus> findByStatus(PatreonPledgeStatus status);
}
