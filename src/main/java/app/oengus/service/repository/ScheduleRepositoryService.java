package app.oengus.service.repository;

import app.oengus.dao.ScheduleRepository;
import app.oengus.entity.model.Marathon;
import app.oengus.entity.model.Schedule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ScheduleRepositoryService {

	@Autowired
	private ScheduleRepository scheduleRepository;

	public Schedule findByMarathon(final Marathon marathon) {
		return this.scheduleRepository.findByMarathon(marathon);
	}

	public void save(final Schedule schedule) {
		this.scheduleRepository.save(schedule);
	}

	public void deleteByMarathon(final Marathon marathon) {
		this.scheduleRepository.deleteByMarathon(marathon);
	}
}
