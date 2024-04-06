package app.oengus.service.repository;

import app.oengus.dao.SubmissionRepository;
import app.oengus.adapter.rest.dto.v2.marathon.SubmissionDto;
import app.oengus.entity.model.Marathon;
import app.oengus.entity.model.Status;
import app.oengus.entity.model.Submission;
import app.oengus.adapter.jpa.entity.User;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SubmissionRepositoryService {

	private final SubmissionRepository submissionRepository;
    private final int pageSize;

    public SubmissionRepositoryService(SubmissionRepository submissionRepository, @Value("${oengus.pageSize}") int pageSize) {
        this.submissionRepository = submissionRepository;
        this.pageSize = pageSize;
    }

    ///////////
    // V2 stuff

    public List<SubmissionDto> getToplevelDataForMarathon(final Marathon marathon) {
        return this.submissionRepository.findByMarathonToplevel(marathon)
            .stream()
            .map((rwData) -> {
                var data = new SubmissionDto();

                data.setSubmissionId((Integer) rwData.get("id"));
                data.setUserId((Integer) rwData.get("userId"));
                data.setUsername((String) rwData.get("username"));
                data.setDisplayName((String) rwData.get("displayName"));
                data.setTotal((Long) rwData.get("total"));

                return data;
            })
            .toList();
    }

    ////////////
    // Old stuff

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
        return this.submissionRepository.searchForMarathon(marathon, query, PageRequest.of(0, this.pageSize)).getContent();
    }

    public List<Submission> searchForMarathonWithStatus(final Marathon marathon, String query, Status status) {
        return this.submissionRepository.searchForMarathonWithStatus(marathon, query, status, PageRequest.of(0, this.pageSize)).getContent();
    }

	public Page<Submission> findByMarathon(final Marathon marathon, int page) {
		return this.submissionRepository.findByMarathonOrderByIdAsc(marathon, PageRequest.of(page, this.pageSize));
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
