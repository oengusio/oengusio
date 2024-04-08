package app.oengus.service.repository;

import app.oengus.dao.DonationIncentiveLinkRepository;
import app.oengus.entity.model.Bid;
import app.oengus.entity.model.Incentive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Deprecated(forRemoval = true)
public class DonationIncentiveLinkRepositoryService {

	@Autowired
	private DonationIncentiveLinkRepository donationIncentiveLinkRepository;

	public void deleteByIncentive(final Incentive incentive) {
		this.donationIncentiveLinkRepository.deleteByIncentive(incentive);
	}

	public void deleteByBid(final Bid bid) {
		this.donationIncentiveLinkRepository.deleteByBid(bid);
	}

}
