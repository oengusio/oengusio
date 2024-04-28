package app.oengus.adapter.jpa.repository;

import app.oengus.adapter.jpa.entity.MarathonEntity;
import app.oengus.adapter.jpa.entity.ScheduleEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface ScheduleRepository extends CrudRepository<ScheduleEntity, Integer> {
    Optional<ScheduleEntity> findByMarathonAndId(MarathonEntity marathon, int id);

    List<ScheduleEntity> findByMarathonOrderByIdAsc(MarathonEntity marathon);

    void deleteByMarathon(MarathonEntity marathon);

    boolean existsByMarathonAndSlug(MarathonEntity marathon, String slug);
}
