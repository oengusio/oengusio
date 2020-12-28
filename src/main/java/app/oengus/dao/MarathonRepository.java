package app.oengus.dao;

import app.oengus.entity.model.Marathon;
import app.oengus.entity.model.User;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.QueryHint;
import java.time.ZonedDateTime;
import java.util.List;

@Repository
public interface MarathonRepository extends JpaRepository<Marathon, String> {

	@Query(value = "SELECT m from Marathon m WHERE m.startDate > current_timestamp AND m.isPrivate = FALSE " +
			"ORDER BY m.startDate ASC")
	@QueryHints(@QueryHint(name = org.hibernate.annotations.QueryHints.CACHEABLE, value = "true"))
	List<Marathon> findNext(Pageable pageable);

	default List<Marathon> findNext() {
		return this.findNext(PageRequest.of(0, 5));
	}

	@Query(value =
			"SELECT m from Marathon m WHERE m.startDate > current_timestamp AND m.submitsOpen = TRUE " +
					"AND m.isPrivate = FALSE ORDER BY m.startDate ASC")
	@QueryHints(@QueryHint(name = org.hibernate.annotations.QueryHints.CACHEABLE, value = "true"))
	List<Marathon> findBySubmitsOpenTrue();

	@Query(value =
			"SELECT m from Marathon m WHERE m.startDate < current_timestamp AND m.endDate > current_timestamp " +
					"AND m.scheduleDone = TRUE AND m.isPrivate = FALSE ORDER BY m.startDate ASC")
	@QueryHints(@QueryHint(name = org.hibernate.annotations.QueryHints.CACHEABLE, value = "true"))
	List<Marathon> findLive();

	@Query(value =
			"SELECT m from Marathon m WHERE (m.startDate > :start AND m.endDate < :end " +
					"OR m.startDate < :start AND m.endDate > :end " +
					"OR m.startDate < :start AND m.endDate > :start AND m.endDate < :end " +
					"OR m.startDate > :start AND m.startDate < :end AND m.endDate > :end)" +
					"AND m.isPrivate = FALSE ORDER BY m.startDate ASC")
	@QueryHints(@QueryHint(name = org.hibernate.annotations.QueryHints.CACHEABLE, value = "true"))
	List<Marathon> findBetween(@Param("start") ZonedDateTime start, @Param("end") ZonedDateTime end);

	List<Marathon> findByClearedFalseAndEndDateBefore(ZonedDateTime endDate);

	@Query(value =
			"SELECT DISTINCT m from Marathon m " +
					"LEFT JOIN m.moderators u " +
					"WHERE m.endDate > current_timestamp " +
					"AND (m.creator = :user OR u = :user)" +
					"ORDER BY m.startDate ASC")
	@QueryHints(@QueryHint(name = org.hibernate.annotations.QueryHints.CACHEABLE, value = "true"))
	List<Marathon> findActiveMarathonsByCreatorOrModerator(@Param("user") User user);

	@Query(value =
			"SELECT DISTINCT m from Marathon m " +
					"LEFT JOIN m.moderators u " +
					"WHERE (m.creator = :user OR u = :user)" +
					"ORDER BY m.startDate ASC")
	@QueryHints(@QueryHint(name = org.hibernate.annotations.QueryHints.CACHEABLE, value = "true"))
	List<Marathon> findAllMarathonsByCreatorOrModerator(@Param("user") User user);

	@Modifying
	@Query("UPDATE Marathon m SET m.cleared = true WHERE m = :marathon")
	void clearMarathon(Marathon marathon);

	@Query(value = "SELECT m from Marathon m WHERE m.submissionsEndDate > current_timestamp " +
			"ORDER BY m.submissionsStartDate ASC")
	List<Marathon> findFutureMarathonsWithScheduledSubmissions();

	@Query(value = "SELECT m from Marathon m WHERE m.startDate > current_timestamp AND m.scheduleDone = TRUE " +
			"ORDER BY m.startDate ASC")
	List<Marathon> findFutureMarathonsWithScheduleDone();

	@Modifying
	@Query("UPDATE Marathon m SET m.submitsOpen = true WHERE m = :marathon")
	void openSubmissions(Marathon marathon);

	@Modifying
	@Query("UPDATE Marathon m SET m.submitsOpen = false WHERE m = :marathon")
	void closeSubmissions(Marathon marathon);

}
