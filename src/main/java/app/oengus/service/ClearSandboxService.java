package app.oengus.service;

import app.oengus.dao.MarathonRepository;
import app.oengus.dao.UserRepository;
import javassist.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Profile("sandbox")
public class ClearSandboxService {
    private static final Logger LOG = LoggerFactory.getLogger(ClearSandboxService.class);

    @Autowired
    private MarathonRepository marathonRepository;

    @Autowired
    private MarathonService marathonService;

    @Autowired
    private UserRepository userRepository;

    @Scheduled(cron = "@weekly")
    public void purgeEntries() {
        LOG.info("Deleting marathons");

        this.marathonRepository.findAll().forEach((marathon) -> {
            try {
                this.marathonService.delete(marathon.getId());
            } catch (NotFoundException e) {
                LOG.error("Failed to delete marathon " + marathon.getId(), e);
            }
        });

        LOG.info("Deleting users");

        this.userRepository.deleteAll();
    }
}
