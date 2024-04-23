package app.oengus.adapter.jpa.repository;

import app.oengus.adapter.jpa.entity.Donation;
import app.oengus.adapter.jpa.entity.MarathonEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;

public interface DonationRepository extends PagingAndSortingRepository<Donation, Integer> {

    Page<Donation> findByMarathonAndApprovedIsTrue(MarathonEntity marathon, Pageable pageable);

    Donation findByFunctionalId(String functionalId);

    @Query("SELECT SUM(d.amount) FROM Donation d WHERE d.marathon = :marathon AND d.approved = true")
    BigDecimal findTotalAmountByMarathon(@Param("marathon") MarathonEntity marathon);

    @Query("SELECT AVG(d.amount) FROM Donation d WHERE d.marathon = :marathon AND d.approved = true")
    BigDecimal findAverageAmountByMarathon(@Param("marathon") MarathonEntity marathon);

    @Query("SELECT MAX(d.amount) FROM Donation d WHERE d.marathon = :marathon AND d.approved = true")
    BigDecimal findMaxAmountByMarathon(@Param("marathon") MarathonEntity marathon);

    int countByMarathonAndApprovedIsTrue(MarathonEntity marathon);

    void deleteByFunctionalId(String functionalId);
}
