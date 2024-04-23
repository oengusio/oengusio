package app.oengus.adapter.jpa.repository;

import app.oengus.adapter.jpa.entity.DonationExtraData;
import app.oengus.adapter.jpa.entity.MarathonEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DonationExtraDataRepository extends JpaRepository<DonationExtraData, Integer> {

    @Modifying
    @Query("DELETE FROM DonationExtraData ded WHERE ded.id IN (SELECT sded.id FROM DonationExtraData sded WHERE sded" +
        ".donation.marathon = :marathon)")
    void deleteByMarathon(@Param(value = "marathon") MarathonEntity marathon);

}
