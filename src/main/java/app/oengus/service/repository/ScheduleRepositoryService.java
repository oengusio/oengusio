package app.oengus.service.repository;

import app.oengus.dao.ScheduleRepository;
import app.oengus.adapter.jpa.entity.MarathonEntity;
import app.oengus.entity.model.Schedule;
import javassist.NotFoundException;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ScheduleRepositoryService {

	private final ScheduleRepository scheduleRepository;

    public ScheduleRepositoryService(ScheduleRepository scheduleRepository) {
        this.scheduleRepository = scheduleRepository;
    }

	public List<Schedule> findAllByMarathon(final MarathonEntity marathon) {
        return this.scheduleRepository.findByMarathon(marathon);
	}

	public Schedule findById(final MarathonEntity marathon, final int scheduleId) throws NotFoundException {
        return this.scheduleRepository.findByMarathonAndId(marathon, scheduleId)
            .orElseThrow(() -> new NotFoundException("Schedule with id " + scheduleId + " not found."));
	}

    @Nullable
	public Schedule findByMarathon(final MarathonEntity marathon) {
        final List<Schedule> schedules = this.scheduleRepository.findByMarathon(marathon);

        if (schedules.isEmpty()) {
            return null;
        }

        return schedules.get(0);
	}

	public void save(final Schedule schedule) {
		this.scheduleRepository.save(schedule);
	}

	public void deleteByMarathon(final MarathonEntity marathon) {
		this.scheduleRepository.deleteByMarathon(marathon);
	}
}
