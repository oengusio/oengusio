package app.oengus.service;

import app.oengus.entity.model.Marathon;
import app.oengus.entity.model.Schedule;
import app.oengus.entity.model.ScheduleLine;
import app.oengus.service.repository.MarathonRepositoryService;
import app.oengus.service.repository.ScheduleRepositoryService;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class ScheduleService {

	@Autowired
	private ScheduleRepositoryService scheduleRepository;

	@Autowired
	private MarathonRepositoryService marathonRepositoryService;

	@Transactional
	public Schedule findByMarathon(final String marathonId) {
		final Marathon marathon = new Marathon();
		marathon.setId(marathonId);
		final Schedule schedule = this.scheduleRepository.findByMarathon(marathon);
		if (schedule != null && schedule.getLines() != null && schedule.getLines().size() > 0) {
			schedule.getLines().get(0).setDate(schedule.getMarathon().getStartDate());
			for (int i = 1; i < schedule.getLines().size(); i++) {
				final ScheduleLine previous = schedule.getLines().get(i - 1);
				schedule.getLines()
				        .get(i)
				        .setDate(
						        previous.getDate().plus(previous.getEstimate()).plus(previous.getSetupTime()));
			}
		}
		return schedule;
	}

	@Transactional
	public void deleteByMarathon(final String marathonId) {
		final Marathon marathon = new Marathon();
		marathon.setId(marathonId);
		this.scheduleRepository.deleteByMarathon(marathon);
	}

	@Transactional
	public void saveOrUpdate(final String marathonId, final Schedule schedule) throws NotFoundException {
		final Marathon marathon =
				this.marathonRepositoryService.findById(marathonId);
		schedule.setMarathon(marathon);
		schedule.getLines().forEach(scheduleLine -> scheduleLine.setSchedule(schedule));
		this.scheduleRepository.save(schedule);
		if (marathon.isScheduleDone()) {
			this.computeEndDate(marathon, schedule);
			this.marathonRepositoryService.update(marathon);
		}
	}

	public void computeEndDate(final Marathon marathon, final Schedule schedule) {
		marathon.setEndDate(marathon.getStartDate());
		schedule.getLines().forEach(scheduleLine -> {
			marathon.setEndDate(
					marathon.getEndDate().plus(scheduleLine.getEstimate()).plus(scheduleLine.getSetupTime()));
		});
	}
}
