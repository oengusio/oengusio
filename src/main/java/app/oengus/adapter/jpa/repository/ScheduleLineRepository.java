package app.oengus.adapter.jpa.repository;

import app.oengus.adapter.jpa.entity.ScheduleLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ScheduleLineRepository extends JpaRepository<ScheduleLine, Integer> {
    @Modifying
    @Query(
        value = "UPDATE schedule_line_runner SET user_id = :newUseId WHERE user_id = :oldUserId",
        nativeQuery = true
    )
    void updateLinesSetUserIdWhereNewUserId(@Param(value = "oldUserId") int oldUserId, @Param(value = "newUseId") int newUserId);
}
