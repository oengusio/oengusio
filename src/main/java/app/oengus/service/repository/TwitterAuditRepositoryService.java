package app.oengus.service.repository;

import app.oengus.dao.TwitterAuditRepository;
import app.oengus.adapter.jpa.entity.MarathonEntity;
import app.oengus.entity.model.TwitterAudit;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Deprecated(forRemoval = true)
public class TwitterAuditRepositoryService {

	private final TwitterAuditRepository twitterAuditRepository;

    public TwitterAuditRepositoryService(TwitterAuditRepository twitterAuditRepository) {
        this.twitterAuditRepository = twitterAuditRepository;
    }

    public void save(final MarathonEntity marathon, final String action) {
		this.twitterAuditRepository.save(new TwitterAudit(marathon, action));
	}

	public boolean exists(final MarathonEntity marathon, final String action) {
		return this.twitterAuditRepository.existsByMarathonAndAction(marathon, action);
	}

	@Transactional
	public void deleteByMarathon(final MarathonEntity marathon) {
		this.twitterAuditRepository.deleteByMarathon(marathon);
	}

}
