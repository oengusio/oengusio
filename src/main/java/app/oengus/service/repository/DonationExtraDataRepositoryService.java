package app.oengus.service.repository;

import app.oengus.dao.DonationExtraDataRepository;
import app.oengus.entity.model.Marathon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class DonationExtraDataRepositoryService {

	@Autowired
	private DonationExtraDataRepository donationExtraDataRepository;

	@Transactional
	public void deleteByMarathon(final Marathon marathon) {
		this.donationExtraDataRepository.deleteByMarathon(marathon);
	}

}
