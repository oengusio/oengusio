package app.oengus.application;

import app.oengus.application.port.persistence.MarathonPersistencePort;
import app.oengus.application.port.persistence.SchedulePersistencePort;
import app.oengus.application.port.persistence.UserPersistencePort;
import app.oengus.domain.marathon.Marathon;
import app.oengus.domain.schedule.Schedule;
import app.oengus.factory.OengusUserFactory;
import app.oengus.factory.marathon.MarathonFactory;
import app.oengus.util.ScheduleHelpers;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class ScheduleServiceTests {
    private final MarathonFactory marathonFactory;
    private final OengusUserFactory userFactory;
    private final MarathonPersistencePort marathonPersistenceAdapter;
    private final SchedulePersistencePort schedulePersistenceAdapter;
    private final UserPersistencePort userPersistencePort;

    private final ScheduleHelpers scheduleHelpers;

    private final MarathonService marathonService;
    private final ScheduleService scheduleService;

    @Test // Regression https://github.com/oengusio/oengusio/issues/342
    public void testCreatingScheduleForMarathonThatHasExistingScheduleDoesNotRecomputeEndDate() {
        var marathon = this.createTestMarathon();
        final var schedule1 = this.createAndPublishTestSchedule(marathon);

        marathon = this.marathonPersistenceAdapter.findById(marathon.getId()).get();

        final var endDate = marathon.getEndDate();

        final var newDummySchedule = this.scheduleHelpers.createSchedule(marathon.getId());

        // make lines empty to simulate creating new schedule
        newDummySchedule.setLines(List.of());

        this.scheduleService.saveOrUpdate(marathon.getId(), newDummySchedule);

        final var updatedMarathon = this.marathonPersistenceAdapter.findById(marathon.getId()).get();

        assertEquals(endDate, updatedMarathon.getEndDate(), "Marathon end date should not change after creating new dummy schedule");
    }

    private Marathon createTestMarathon() {
        final var user = this.userFactory.getObject();
        final var creator = this.userPersistencePort.save(user);
        final var marathon = this.marathonFactory.withCreator(creator);

        marathon.setStartDate(marathon.getStartDate().plusSeconds(20));
        marathon.setEndDate(marathon.getEndDate().plusSeconds(20));

        return marathon;
    }

    private Schedule createAndPublishTestSchedule(Marathon marathon) {
        final var schedule = this.scheduleHelpers.createSchedule(marathon.getId());

        schedule.setPublished(true);

        marathon.setScheduleDone(true);

        this.marathonPersistenceAdapter.save(marathon);

        return this.scheduleService.saveOrUpdate(marathon.getId(), schedule);
    }
}
