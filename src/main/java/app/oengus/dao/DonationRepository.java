package app.oengus.dao;

import app.oengus.entity.model.Donation;
import app.oengus.entity.model.Marathon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface DonationRepository extends PagingAndSortingRepository<Donation, Integer> {

    Page<Donation> findByMarathonAndApprovedIsTrue(Marathon marathon, Pageable pageable);

    Donation findByFunctionalId(String functionalId);

    @Query("SELECT SUM(d.amount) FROM Donation d WHERE d.marathon = :marathon AND d.approved = true")
    BigDecimal findTotalAmountByMarathon(@Param("marathon") Marathon marathon);

    @Query("SELECT AVG(d.amount) FROM Donation d WHERE d.marathon = :marathon AND d.approved = true")
    BigDecimal findAverageAmountByMarathon(@Param("marathon") Marathon marathon);

    @Query("SELECT MAX(d.amount) FROM Donation d WHERE d.marathon = :marathon AND d.approved = true")
    BigDecimal findMaxAmountByMarathon(@Param("marathon") Marathon marathon);

    int countByMarathonAndApprovedIsTrue(Marathon marathon);

    void deleteByFunctionalId(String functionalId);
}
