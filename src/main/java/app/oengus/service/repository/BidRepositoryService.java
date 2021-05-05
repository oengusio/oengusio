package app.oengus.service.repository;

import app.oengus.dao.BidRepository;
import app.oengus.entity.model.Bid;
import app.oengus.entity.model.Marathon;
import app.oengus.helper.MathUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BidRepositoryService {

	@Autowired
	private BidRepository bidRepository;

	public Map<Integer, BigDecimal> findAmountsByMarathon(final String marathonId) {
		final Marathon marathon = new Marathon();
		marathon.setId(marathonId);
		final List<Object[]> results = this.bidRepository.findAmountsByMarathon(marathon);
		final Map<Integer, BigDecimal> map = new HashMap<>();
		results.forEach(result -> {
			map.put((int) result[0], MathUtils.getBigDecimal(result[1]));
		});
		return map;
	}

	public Bid save(final Bid bid) {
		return this.bidRepository.save(bid);
	}

	public void delete(final Bid bid) {
		this.bidRepository.delete(bid);
	}

}
