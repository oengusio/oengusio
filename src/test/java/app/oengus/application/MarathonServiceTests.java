package app.oengus.application;

import app.oengus.domain.marathon.Marathon;
import app.oengus.factory.OengusUserFactory;
import app.oengus.factory.marathon.MarathonFactory;
import app.oengus.mock.adapter.jpa.MockMarathonPersistenceAdapter;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class MarathonServiceTests {
    private final MarathonFactory marathonFactory;
    private final OengusUserFactory userFactory;
    private final MockMarathonPersistenceAdapter marathonPersistenceAdapter;

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
        final var marathon = this.createTestMarathon();
        final var creator = marathon.getCreator();

        this.marathonService.create(marathon);

        final var optionalUser = this.marathonService.findCreatorById(marathon.getId());

        assertTrue(optionalUser.isPresent());
        assertEquals(creator, optionalUser.get());
    }

    private Marathon createTestMarathon() {
        final var marathon = this.marathonFactory.getObject();

        marathon.setStartDate(marathon.getStartDate().plusSeconds(20));
        marathon.setEndDate(marathon.getEndDate().plusSeconds(20));

        return marathon;
    }
}
