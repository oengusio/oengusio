package app.oengus.service.repository;

import app.oengus.dao.IncentiveRepository;
import app.oengus.entity.model.Incentive;
import app.oengus.entity.model.Marathon;
import app.oengus.helper.MathUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class IncentiveRepositoryService {

	@Autowired
	private IncentiveRepository incentiveRepository;

	public List<Incentive> findByMarathon(final String marathonId) {
		final Marathon marathon = new Marathon();
		marathon.setId(marathonId);
		return this.incentiveRepository.findByMarathon(marathon);
	}

	public List<Incentive> findByMarathonNotLocked(final String marathonId) {
		final Marathon marathon = new Marathon();
		marathon.setId(marathonId);
		return this.incentiveRepository.findByMarathonNotLocked(marathon);
	}

	public Map<Integer, BigDecimal> findAmountsByMarathon(final String marathonId) {
		final Marathon marathon = new Marathon();
		marathon.setId(marathonId);
		final List<Object[]> results = this.incentiveRepository.findAmountsByMarathon(marathon);
		final Map<Integer, BigDecimal> map = new HashMap<>();
		results.forEach(result -> {
			map.put((int) result[0], MathUtils.getBigDecimal(result[1]));
		});
		return map;
	}

	public List<Incentive> saveAll(final List<Incentive> incentive) {
		return this.incentiveRepository.saveAll(incentive);
	}

	public void delete(final int incentiveId) {
		this.incentiveRepository.deleteById(incentiveId);
	}

	public void delete(final String marathonId) {
		final Marathon marathon = new Marathon();
		marathon.setId(marathonId);
		this.incentiveRepository.deleteByMarathon(marathon);
	}

}
