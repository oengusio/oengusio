package app.oengus.application;

import app.oengus.application.port.persistence.MarathonPersistencePort;
import app.oengus.application.port.persistence.SchedulePersistencePort;
import app.oengus.application.port.persistence.UserPersistencePort;
import app.oengus.domain.marathon.Marathon;
import app.oengus.factory.OengusUserFactory;
import app.oengus.factory.marathon.MarathonFactory;
import app.oengus.util.ObjectCloner;
import app.oengus.util.ScheduleHelpers;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class MarathonServiceTests {
    private final MarathonFactory marathonFactory;
    private final OengusUserFactory userFactory;
    private final MarathonPersistencePort marathonPersistenceAdapter;
    private final SchedulePersistencePort schedulePersistenceAdapter;
    private final UserPersistencePort userPersistencePort;

    private final ScheduleHelpers scheduleHelpers;
    private final ObjectCloner cloner;

    private final MarathonService marathonService;

    @Test
    public void testCreateMarathonStripsSecondsFromStartAndEndDate() {
        final var marathon = this.createTestMarathon();

        final var startDate = marathon.getStartDate();
        final var endDate = marathon.getEndDate();

        final var createdMarathon = this.marathonService.create(marathon);

        assertNotEquals(startDate, createdMarathon.getStartDate());
        assertNotEquals(endDate, createdMarathon.getEndDate());

        assertEquals(startDate.withSecond(0), createdMarathon.getStartDate());
        assertEquals(endDate.withSecond(0), createdMarathon.getEndDate());
    }

    @Test
    public void testFakeMarathonDoesNotExist() {
        assertFalse(
            this.marathonService.exists("FAKE_MARATHON_ID")
        );
    }

    @Test
    public void testCreatorCanBeRetrieved() {
        var marathon = this.createTestMarathon();
        final var creator = marathon.getCreator();

        marathon = this.marathonService.create(marathon);

        final var optionalUser = this.marathonService.findCreatorById(marathon.getId());

        assertTrue(optionalUser.isPresent());
        assertEquals(creator, optionalUser.get());
    }

    @Test
    public void testMarathonUpdateSetsEndTimeForScheduleCorrectly() {
        var marathon = this.createTestMarathon();

        marathon = this.cloner.clone(this.marathonService.create(marathon));
        final var startDate = marathon.getStartDate();

        final var schedule = this.scheduleHelpers.createSchedule(marathon.getId());
        final var lineCount = schedule.getLines().size();
        final var fiveMin = Duration.ofMinutes(5);
        // Each line gets a setup and estimate of 5 minutes making 10 minutes per line.
        final var expectedEndTime = startDate.plusMinutes(lineCount * 10L);

        assertNotEquals(startDate, expectedEndTime);

        schedule.getLines().forEach(line -> {
            line.setEstimate(fiveMin);
            line.setSetupTime(fiveMin);
        });
        schedule.setPublished(true);

        this.schedulePersistenceAdapter.save(schedule);

        marathon.setScheduleDone(true);

        final var updatedMarathon = this.marathonService.update(marathon.getId(), marathon);

        assertEquals(startDate, updatedMarathon.getStartDate(), "Start date should not change");
        assertEquals(expectedEndTime, updatedMarathon.getEndDate(), "End date should match expected end date");
    }

    private Marathon createTestMarathon() {
        final var user = this.userFactory.getObject();
        final var creator = this.userPersistencePort.save(user);
        final var marathon = this.marathonFactory.withCreator(creator);

        marathon.setStartDate(marathon.getStartDate().plusSeconds(20));
        marathon.setEndDate(marathon.getEndDate().plusSeconds(20));

        return marathon;
    }
}
