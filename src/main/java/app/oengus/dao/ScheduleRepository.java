package app.oengus.dao;

import app.oengus.adapter.jpa.entity.MarathonEntity;
import app.oengus.entity.model.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Integer> {

    Optional<Schedule> findByMarathonAndId(MarathonEntity marathon, int id);

    List<Schedule> findByMarathon(MarathonEntity marathon);

    void deleteByMarathon(MarathonEntity marathon);
}
