package app.oengus.adapter.jpa.repository;

import app.oengus.adapter.jpa.entity.PatreonStatus;
import app.oengus.domain.PatreonPledgeStatus;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PatreonStatusRepository extends CrudRepository<PatreonStatus, String> {
    List<PatreonStatus> findByStatus(PatreonPledgeStatus status);
}
