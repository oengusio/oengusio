package app.oengus.dao;

import app.oengus.entity.model.Marathon;
import app.oengus.entity.model.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;

import javax.persistence.QueryHint;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Integer> {

	@QueryHints(@QueryHint(name = org.hibernate.annotations.QueryHints.CACHEABLE, value = "true"))
	Schedule findByMarathon(Marathon marathon);

	void deleteByMarathon(Marathon marathon);

}
