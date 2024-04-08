package app.oengus.service.repository;

import app.oengus.dao.DonationExtraDataRepository;
import app.oengus.adapter.jpa.entity.MarathonEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Deprecated(forRemoval = true)
public class DonationExtraDataRepositoryService {

	@Autowired
	private DonationExtraDataRepository donationExtraDataRepository;

	@Transactional
	public void deleteByMarathon(final MarathonEntity marathon) {
		this.donationExtraDataRepository.deleteByMarathon(marathon);
	}

}
