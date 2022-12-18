package app.oengus.dao;

import app.oengus.entity.model.Marathon;
import app.oengus.entity.model.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Integer> {

    Schedule findByMarathon(Marathon marathon);

    void deleteByMarathon(Marathon marathon);
}
