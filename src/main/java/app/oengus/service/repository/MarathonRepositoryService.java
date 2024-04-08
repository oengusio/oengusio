package app.oengus.service.repository;

import app.oengus.adapter.jpa.repository.MarathonRepository;
import app.oengus.entity.dto.marathon.MarathonStatsDto;
import app.oengus.adapter.jpa.entity.MarathonEntity;
import app.oengus.adapter.jpa.entity.User;
import javassist.NotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.ZonedDateTime;
import java.util.List;

@Service
@Deprecated(forRemoval = true)
public class MarathonRepositoryService {

	private final MarathonRepository marathonRepository;

    public MarathonRepositoryService(MarathonRepository marathonRepository) {
        this.marathonRepository = marathonRepository;
    }

    public MarathonEntity save(final MarathonEntity marathon) {
		return this.marathonRepository.save(marathon);
	}

	public MarathonEntity update(final MarathonEntity marathon) {
		return this.marathonRepository.save(marathon);
	}

	public boolean existsById(final String name) {
		return this.marathonRepository.existsById(name);
	}

	public MarathonEntity findById(final String id) throws NotFoundException {
		return this.marathonRepository.findById(id)
		                              .orElseThrow(() -> new NotFoundException("Marathon not found"));
	}

	public String getNameById(String id) {
	    return this.marathonRepository.getNameById(id);
    }

	public void delete(final MarathonEntity marathon) {
		this.marathonRepository.delete(marathon);
	}

	public List<MarathonEntity> findNext() {
		return this.marathonRepository.findNext();
	}

	public List<MarathonEntity> findBetween(final ZonedDateTime start, final ZonedDateTime end) {
		return this.marathonRepository.findBetween(start, end);
	}

	public List<MarathonEntity> findBySubmitsOpenTrue() {
		return this.marathonRepository.findBySubmitsOpenTrue();
	}

	public List<MarathonEntity> findLive() {
		return this.marathonRepository.findLive();
	}

	public List<MarathonEntity> findActiveMarathonsByCreatorOrModerator(final User user) {
		return this.marathonRepository.findActiveMarathonsByCreatorOrModerator(user);
	}

	public List<MarathonEntity> findAllMarathonsByCreatorOrModerator(final User user) {
		return this.marathonRepository.findAllMarathonsByCreatorOrModerator(user);
	}

	public List<MarathonEntity> findByClearedFalseAndEndDateBefore(final ZonedDateTime endDate) {
		return this.marathonRepository.findByClearedFalseAndEndDateBefore(endDate);
	}

	public List<MarathonEntity> findFutureMarathonsWithScheduledSubmissions() {
		return this.marathonRepository.findFutureMarathonsWithScheduledSubmissions();
	}

	public List<MarathonEntity> findFutureMarathonsWithScheduleDone() {
		return this.marathonRepository.findFutureMarathonsWithScheduleDone();
	}

    public MarathonStatsDto findStats(final MarathonEntity marathon) throws NotFoundException {
        return this.marathonRepository.findStats(marathon)
            .map(MarathonStatsDto::new)
            .orElseThrow(() -> new NotFoundException("Marathon not found"));
    }

    @Transactional
	public void clearMarathon(final MarathonEntity marathon) {
		this.marathonRepository.clearMarathon(marathon);
	}

	@Transactional
	public void openSubmissions(final MarathonEntity marathon) {
		this.marathonRepository.openSubmissions(marathon);
	}

	@Transactional
	public void closeSubmissions(final MarathonEntity marathon) {
		this.marathonRepository.closeSubmissions(marathon);
	}
}
