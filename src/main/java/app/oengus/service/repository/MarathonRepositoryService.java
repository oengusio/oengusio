package app.oengus.service.repository;

import app.oengus.dao.MarathonRepository;
import app.oengus.entity.dto.marathon.MarathonStatsDto;
import app.oengus.entity.model.Marathon;
import app.oengus.adapter.jpa.entity.User;
import javassist.NotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.ZonedDateTime;
import java.util.List;

@Service
public class MarathonRepositoryService {

	private final MarathonRepository marathonRepository;

    public MarathonRepositoryService(MarathonRepository marathonRepository) {
        this.marathonRepository = marathonRepository;
    }

    public Marathon save(final Marathon marathon) {
		return this.marathonRepository.save(marathon);
	}

	public Marathon update(final Marathon marathon) {
		return this.marathonRepository.save(marathon);
	}

	public boolean existsById(final String name) {
		return this.marathonRepository.existsById(name);
	}

	public Marathon findById(final String id) throws NotFoundException {
		return this.marathonRepository.findById(id)
		                              .orElseThrow(() -> new NotFoundException("Marathon not found"));
	}

	public String getNameById(String id) {
	    return this.marathonRepository.getNameById(id);
    }

	public void delete(final Marathon marathon) {
		this.marathonRepository.delete(marathon);
	}

	public List<Marathon> findNext() {
		return this.marathonRepository.findNext();
	}

	public List<Marathon> findBetween(final ZonedDateTime start, final ZonedDateTime end) {
		return this.marathonRepository.findBetween(start, end);
	}

	public List<Marathon> findBySubmitsOpenTrue() {
		return this.marathonRepository.findBySubmitsOpenTrue();
	}

	public List<Marathon> findLive() {
		return this.marathonRepository.findLive();
	}

	public List<Marathon> findActiveMarathonsByCreatorOrModerator(final User user) {
		return this.marathonRepository.findActiveMarathonsByCreatorOrModerator(user);
	}

	public List<Marathon> findAllMarathonsByCreatorOrModerator(final User user) {
		return this.marathonRepository.findAllMarathonsByCreatorOrModerator(user);
	}

	public List<Marathon> findByClearedFalseAndEndDateBefore(final ZonedDateTime endDate) {
		return this.marathonRepository.findByClearedFalseAndEndDateBefore(endDate);
	}

	public List<Marathon> findFutureMarathonsWithScheduledSubmissions() {
		return this.marathonRepository.findFutureMarathonsWithScheduledSubmissions();
	}

	public List<Marathon> findFutureMarathonsWithScheduleDone() {
		return this.marathonRepository.findFutureMarathonsWithScheduleDone();
	}

    public MarathonStatsDto findStats(final Marathon marathon) throws NotFoundException {
        return this.marathonRepository.findStats(marathon)
            .map(MarathonStatsDto::new)
            .orElseThrow(() -> new NotFoundException("Marathon not found"));
    }

    @Transactional
	public void clearMarathon(final Marathon marathon) {
		this.marathonRepository.clearMarathon(marathon);
	}

	@Transactional
	public void openSubmissions(final Marathon marathon) {
		this.marathonRepository.openSubmissions(marathon);
	}

	@Transactional
	public void closeSubmissions(final Marathon marathon) {
		this.marathonRepository.closeSubmissions(marathon);
	}
}
