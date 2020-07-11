package app.oengus.service.repository;

import app.oengus.dao.DonationRepository;
import app.oengus.entity.model.Donation;
import app.oengus.entity.model.Marathon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class DonationRepositoryService {


	@Autowired
	private DonationRepository donationRepository;

	public Page<Donation> findByMarathon(final String marathonId, final Pageable pageable) {
		final Marathon marathon = new Marathon();
		marathon.setId(marathonId);
		return this.donationRepository.findByMarathonAndApprovedIsTrue(marathon, pageable);
	}

	public Donation save(final Donation donation) {
		return this.donationRepository.save(donation);
	}

	public void delete(final String functionalId) {
		this.donationRepository.deleteByFunctionalId(functionalId);
	}

	public Donation findByFunctionalId(final String functionalId) {
		return this.donationRepository.findByFunctionalId(functionalId);
	}

	public BigDecimal findTotalAmountByMarathon(final String marathonId) {
		final Marathon marathon = new Marathon();
		marathon.setId(marathonId);
		BigDecimal result = this.donationRepository.findTotalAmountByMarathon(marathon);
		if (result == null) {
			result = BigDecimal.ZERO;
		}
		return result;
	}

	public BigDecimal findAverageAmountByMarathon(final String marathonId) {
		final Marathon marathon = new Marathon();
		marathon.setId(marathonId);
		BigDecimal result = this.donationRepository.findAverageAmountByMarathon(marathon);
		if (result == null) {
			result = BigDecimal.ZERO;
		}
		return result;
	}

	public BigDecimal findMaxAmountByMarathon(final String marathonId) {
		final Marathon marathon = new Marathon();
		marathon.setId(marathonId);
		BigDecimal result = this.donationRepository.findMaxAmountByMarathon(marathon);
		if (result == null) {
			result = BigDecimal.ZERO;
		}
		return result;
	}

	public Integer countByMarathon(final String marathonId) {
		final Marathon marathon = new Marathon();
		marathon.setId(marathonId);
		return this.donationRepository.countByMarathonAndApprovedIsTrue(marathon);
	}

}
