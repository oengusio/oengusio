package app.oengus.dao;

import app.oengus.entity.model.Bid;
import app.oengus.adapter.jpa.entity.MarathonEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BidRepository extends JpaRepository<Bid, Integer> {

    @Query("SELECT b.id, SUM(dil.amount) FROM Bid b LEFT JOIN b.donationIncentiveLinks dil " +
        "WHERE b.incentive.marathon = :marathon AND dil.donation.approved = true GROUP BY b.id")
    List<Object[]> findAmountsByMarathon(@Param(value = "marathon") MarathonEntity marathon);

}
