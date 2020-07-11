package app.oengus.service.repository;

import app.oengus.dao.SubmissionRepository;
import app.oengus.entity.model.Marathon;
import app.oengus.entity.model.Submission;
import app.oengus.entity.model.User;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SubmissionRepositoryService {

	@Autowired
	private SubmissionRepository submissionRepository;

	public Submission save(final Submission submission) {
		return this.submissionRepository.save(submission);
	}

	public List<Submission> findValidatedOrBonusSubmissionsForMarathon(final Marathon marathon) {
		return this.submissionRepository.findValidatedOrBonusSubmissionsForMarathon(marathon);
	}

	public Submission findByUserAndMarathon(final User user, final Marathon marathon) {
		return this.submissionRepository.findByUserAndMarathon(user, marathon);
	}

	public List<Submission> findByMarathon(final Marathon marathon) {
		return this.submissionRepository.findByMarathon(marathon);
	}

	public List<Submission> findByUser(final User user) {
		return this.submissionRepository.findByUser(user);
	}

	public List<Submission> findCustomAnswersByMarathon(final Marathon marathon) {
		final List<Submission> submissions = this.submissionRepository.findByMarathon(marathon);
		final List<Submission> clearedSubmissions = new ArrayList<>();
		submissions.forEach(submission -> {
			final Submission copy = new Submission();
			copy.setUser(submission.getUser());
			copy.setId(submission.getId());
			copy.setAnswers(submission.getAnswers());
			clearedSubmissions.add(copy);
		});
		return clearedSubmissions;
	}

	public void deleteByMarathon(final Marathon marathon) {
		this.submissionRepository.deleteByMarathon(marathon);
	}

	public void delete(final Integer id) {
		this.submissionRepository.deleteById(id);
	}

	public Boolean existsByMarathonAndUser(final Marathon marathon, final User user) {
		return this.submissionRepository.existsByMarathonAndUser(marathon, user);
	}

	public Submission findById(final Integer id) throws NotFoundException {
		return this.submissionRepository.findById(id).orElseThrow(() -> new NotFoundException("Submission not found"));
	}
}
