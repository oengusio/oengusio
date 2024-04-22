package app.oengus.application.env.sandbox;

import app.oengus.application.MarathonService;
import app.oengus.application.port.persistence.MarathonPersistencePort;
import app.oengus.application.port.persistence.UserPersistencePort;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Profile("sandbox")
@RequiredArgsConstructor
public class ClearSandboxService {
    private static final Logger LOG = LoggerFactory.getLogger(ClearSandboxService.class);

    private final MarathonPersistencePort marathonPersistencePort;
    private final UserPersistencePort userPersistencePort;
    private final MarathonService marathonService;

    @Scheduled(cron = "@weekly")
//    @Scheduled(cron = "* * * * * *")
    public void purgeEntries() {
        LOG.info("Deleting marathons");

        // we need to fetch marathons before deling because stupid applications
        final var marathons = this.marathonPersistencePort.findAll();

//        this.incentiveRepository.deleteAll();
//        this.donationRepository.deleteAll();

        for (final var marathon : marathons) {
            var marathonId = marathon.getId();
            try {
                this.marathonService.delete(marathonId);
            } catch (NotFoundException e) {
                LOG.error("Failed to delete marathon " + marathonId, e);
            }
        }

        LOG.info("Deleting users");

        this.userPersistencePort.deleteAll();
    }
}
