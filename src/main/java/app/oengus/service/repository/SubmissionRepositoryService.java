package app.oengus.service.repository;

import app.oengus.adapter.jpa.repository.SubmissionRepository;
import app.oengus.adapter.rest.dto.v2.marathon.SubmissionDto;
import app.oengus.adapter.jpa.entity.MarathonEntity;
import app.oengus.entity.model.Status;
import app.oengus.adapter.jpa.entity.SubmissionEntity;
import app.oengus.adapter.jpa.entity.User;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Deprecated(forRemoval = true)
public class SubmissionRepositoryService {

	private final SubmissionRepository submissionRepository;
    private final int pageSize;

    public SubmissionRepositoryService(SubmissionRepository submissionRepository, @Value("${oengus.pageSize}") int pageSize) {
        this.submissionRepository = submissionRepository;
        this.pageSize = pageSize;
    }

    ///////////
    // V2 stuff

    public List<SubmissionDto> getToplevelDataForMarathon(final MarathonEntity marathon) {
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

    public SubmissionEntity save(final SubmissionEntity submission) {
		return this.submissionRepository.save(submission);
	}

	public List<SubmissionEntity> findValidatedOrBonusSubmissionsForMarathon(final MarathonEntity marathon) {
		return this.submissionRepository.findValidatedOrBonusSubmissionsForMarathon(marathon);
	}

	public SubmissionEntity findByUserAndMarathon(final User user, final MarathonEntity marathon) {
		return this.submissionRepository.findByUserAndMarathon(user, marathon).orElse(null);
	}

    public List<SubmissionEntity> searchForMarathon(final MarathonEntity marathon, String query) {
        return this.submissionRepository.searchForMarathon(marathon, query, PageRequest.of(0, this.pageSize)).getContent();
    }

    public List<SubmissionEntity> searchForMarathonWithStatus(final MarathonEntity marathon, String query, Status status) {
        return this.submissionRepository.searchForMarathonWithStatus(marathon, query, status, PageRequest.of(0, this.pageSize)).getContent();
    }

	public Page<SubmissionEntity> findByMarathon(final MarathonEntity marathon, int page) {
		return this.submissionRepository.findByMarathonOrderByIdAsc(marathon, PageRequest.of(page, this.pageSize));
	}

	public List<SubmissionEntity> findAllByMarathon(final MarathonEntity marathon) {
		return this.submissionRepository.findByMarathonOrderByIdAsc(marathon);
	}

	public List<SubmissionEntity> findByUser(final User user) {
		return this.submissionRepository.findByUser(user);
	}

	public List<SubmissionEntity> findCustomAnswersByMarathon(final MarathonEntity marathon) {
		final List<SubmissionEntity> submissions = this.submissionRepository.findByMarathonOrderByIdAsc(marathon);
		final List<SubmissionEntity> clearedSubmissions = new ArrayList<>();
		submissions.forEach(submission -> {
			final SubmissionEntity copy = new SubmissionEntity();
			copy.setUser(submission.getUser());
			copy.setId(submission.getId());
			copy.setAnswers(submission.getAnswers());
			clearedSubmissions.add(copy);
		});
		return clearedSubmissions;
	}

	public void deleteByMarathon(final MarathonEntity marathon) {
		this.submissionRepository.deleteByMarathon(marathon);
	}

	public void delete(final int id) {
		this.submissionRepository.deleteById(id);
	}

	public boolean existsByMarathonAndUser(final MarathonEntity marathon, final User user) {
		return this.submissionRepository.existsByMarathonAndUser(marathon, user);
	}

	public SubmissionEntity findById(final int id) throws NotFoundException {
		return this.submissionRepository.findById(id).orElseThrow(() -> new NotFoundException("Submission not found"));
	}
}
