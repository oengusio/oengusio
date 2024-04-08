package app.oengus.dao;

import app.oengus.adapter.jpa.entity.MarathonEntity;
import app.oengus.entity.model.TwitterAudit;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TwitterAuditRepository extends CrudRepository<TwitterAudit, Integer> {

    boolean existsByMarathonAndAction(MarathonEntity marathon, String action);

    @Modifying
    void deleteByMarathon(MarathonEntity marathon);

}
