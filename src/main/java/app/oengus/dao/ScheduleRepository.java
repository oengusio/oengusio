package app.oengus.dao;

import app.oengus.entity.model.Marathon;
import app.oengus.entity.model.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Integer> {

    List<Schedule> findByMarathon(Marathon marathon);

    void deleteByMarathon(Marathon marathon);
}
