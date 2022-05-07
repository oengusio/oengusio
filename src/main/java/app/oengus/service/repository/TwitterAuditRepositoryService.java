package app.oengus.service.repository;

import app.oengus.dao.TwitterAuditRepository;
import app.oengus.entity.model.Marathon;
import app.oengus.entity.model.TwitterAudit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class TwitterAuditRepositoryService {

	private final TwitterAuditRepository twitterAuditRepository;

    public TwitterAuditRepositoryService(TwitterAuditRepository twitterAuditRepository) {
        this.twitterAuditRepository = twitterAuditRepository;
    }

    public void save(final Marathon marathon, final String action) {
		this.twitterAuditRepository.save(new TwitterAudit(marathon, action));
	}

	public boolean exists(final Marathon marathon, final String action) {
		return this.twitterAuditRepository.existsByMarathonAndAction(marathon, action);
	}

	@Transactional
	public void deleteByMarathon(final Marathon marathon) {
		this.twitterAuditRepository.deleteByMarathon(marathon);
	}

}
