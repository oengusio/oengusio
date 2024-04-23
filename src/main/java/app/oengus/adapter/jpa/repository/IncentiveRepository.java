package app.oengus.adapter.jpa.repository;

import app.oengus.adapter.jpa.entity.Incentive;
import app.oengus.adapter.jpa.entity.MarathonEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

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
