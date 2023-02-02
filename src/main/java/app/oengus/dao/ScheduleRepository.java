package app.oengus.dao;

import app.oengus.entity.model.Marathon;
import app.oengus.entity.model.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Integer> {

    Optional<Schedule> findByMarathonAndId(Marathon marathon, int id);

    List<Schedule> findByMarathon(Marathon marathon);

    void deleteByMarathon(Marathon marathon);
}
