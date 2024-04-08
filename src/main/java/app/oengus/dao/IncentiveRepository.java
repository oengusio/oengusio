package app.oengus.dao;

import app.oengus.entity.model.Incentive;
import app.oengus.adapter.jpa.entity.MarathonEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IncentiveRepository extends JpaRepository<Incentive, Integer> {

    @Query("SELECT i FROM Incentive i WHERE i.marathon = :marathon ORDER BY i.scheduleLine.position ASC")
    List<Incentive> findByMarathon(@Param(value = "marathon") MarathonEntity marathon);

    @Query("SELECT i FROM Incentive i WHERE i.marathon = :marathon AND i.locked = false ORDER BY i.scheduleLine" +
        ".position ASC")
    List<Incentive> findByMarathonNotLocked(@Param(value = "marathon") MarathonEntity marathon);

    @Query("SELECT i.id, SUM(dil.amount) FROM Incentive i LEFT JOIN i.donationIncentiveLinks dil WHERE i.marathon = " +
        ":marathon AND dil.donation.approved = true GROUP BY i.id")
    List<Object[]> findAmountsByMarathon(@Param(value = "marathon") MarathonEntity marathon);

    void deleteByMarathon(MarathonEntity marathon);
}
