package app.oengus.service.repository;

import app.oengus.dao.SubmissionRepository;
import app.oengus.entity.model.Marathon;
import app.oengus.entity.model.Submission;
import app.oengus.entity.model.User;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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

    public List<Submission> searchForMarathon(final Marathon marathon, String query) {
        return this.submissionRepository.searchForMarathon(marathon, query, PageRequest.of(0, 10)).getContent();
    }

    // TODO: change back to 10 entries per page
	public Page<Submission> findByMarathon(final Marathon marathon, int page) {
		return this.submissionRepository.findByMarathonOrderByIdAsc(marathon, PageRequest.of(page, 1));
	}

	public List<Submission> findAllByMarathon(final Marathon marathon) {
		return this.submissionRepository.findByMarathonOrderByIdAsc(marathon);
	}

	public List<Submission> findByUser(final User user) {
		return this.submissionRepository.findByUser(user);
	}

	public List<Submission> findCustomAnswersByMarathon(final Marathon marathon) {
		final List<Submission> submissions = this.submissionRepository.findByMarathonOrderByIdAsc(marathon);
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

	public void delete(final int id) {
		this.submissionRepository.deleteById(id);
	}

	public boolean existsByMarathonAndUser(final Marathon marathon, final User user) {
		return this.submissionRepository.existsByMarathonAndUser(marathon, user);
	}

	public Submission findById(final int id) throws NotFoundException {
		return this.submissionRepository.findById(id).orElseThrow(() -> new NotFoundException("Submission not found"));
	}
}
