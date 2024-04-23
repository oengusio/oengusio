package app.oengus.adapter.jpa.repository;

import app.oengus.adapter.jpa.entity.Bid;
import app.oengus.adapter.jpa.entity.DonationIncentiveLink;
import app.oengus.adapter.jpa.entity.Incentive;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DonationIncentiveLinkRepository extends JpaRepository<DonationIncentiveLink, Integer> {

    void deleteByIncentive(Incentive incentive);

    void deleteByBid(Bid bid);

}
