package app.oengus.dao;

import app.oengus.entity.model.Incentive;
import app.oengus.entity.model.Marathon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.QueryHint;
import java.util.List;

@Repository
public interface IncentiveRepository extends JpaRepository<Incentive, Integer> {

    @Query("SELECT i FROM Incentive i WHERE i.marathon = :marathon ORDER BY i.scheduleLine.position ASC")
    @QueryHints(@QueryHint(name = org.hibernate.annotations.QueryHints.CACHEABLE, value = "true"))
    List<Incentive> findByMarathon(@Param(value = "marathon") Marathon marathon);

    @Query("SELECT i FROM Incentive i WHERE i.marathon = :marathon AND i.locked = false ORDER BY i.scheduleLine" +
        ".position ASC")
    @QueryHints(@QueryHint(name = org.hibernate.annotations.QueryHints.CACHEABLE, value = "true"))
    List<Incentive> findByMarathonNotLocked(@Param(value = "marathon") Marathon marathon);

    @Query("SELECT i.id, SUM(dil.amount) FROM Incentive i LEFT JOIN i.donationIncentiveLinks dil WHERE i.marathon = " +
        ":marathon AND dil.donation.approved = true GROUP BY i.id")
    @QueryHints(@QueryHint(name = org.hibernate.annotations.QueryHints.CACHEABLE, value = "true"))
    List<Object[]> findAmountsByMarathon(@Param(value = "marathon") Marathon marathon);

    void deleteByMarathon(Marathon marathon);
}
