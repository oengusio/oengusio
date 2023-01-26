package app.oengus.service.repository;

import app.oengus.dao.ScheduleRepository;
import app.oengus.entity.model.Marathon;
import app.oengus.entity.model.Schedule;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ScheduleRepositoryService {

	private final ScheduleRepository scheduleRepository;

    public ScheduleRepositoryService(ScheduleRepository scheduleRepository) {
        this.scheduleRepository = scheduleRepository;
    }

	public List<Schedule> findAllByMarathon(final Marathon marathon) {
        return this.scheduleRepository.findByMarathon(marathon);
	}

    @Nullable
	public Schedule findByMarathon(final Marathon marathon) {
        final List<Schedule> schedules = this.scheduleRepository.findByMarathon(marathon);

        if (schedules.isEmpty()) {
            return null;
        }

        return schedules.get(0);
	}

	public void save(final Schedule schedule) {
		this.scheduleRepository.save(schedule);
	}

	public void deleteByMarathon(final Marathon marathon) {
		this.scheduleRepository.deleteByMarathon(marathon);
	}
}
