package app.oengus.dao;

import app.oengus.entity.model.Bid;
import app.oengus.entity.model.DonationIncentiveLink;
import app.oengus.entity.model.Incentive;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DonationIncentiveLinkRepository extends JpaRepository<DonationIncentiveLink, Integer> {

    void deleteByIncentive(Incentive incentive);

    void deleteByBid(Bid bid);

}
