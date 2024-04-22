package app.oengus.application;

import app.oengus.application.port.persistence.MarathonPersistencePort;
import app.oengus.application.port.security.UserSecurityPort;
import app.oengus.domain.marathon.Marathon;
import app.oengus.domain.marathon.MarathonStats;
import app.oengus.domain.schedule.Schedule;
import io.sentry.Sentry;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MarathonService {
    private final MarathonPersistencePort marathonPersistencePort;
    private final SubmissionService submissionService;
    private final ScheduleService scheduleService;
    private final IncentiveService incentiveService;
    private final EventSchedulerService eventSchedulerService;
    private final SelectionService selectionService;
    private final OengusWebhookService webhookService;
    private final UserSecurityPort securityPort;

    @PostConstruct
    public void initScheduledEvents() {
        final List<Marathon> marathonsWithScheduledSubmissions =
            this.marathonPersistencePort.findFutureWithScheduledSubmissions();

        marathonsWithScheduledSubmissions.forEach(this.eventSchedulerService::scheduleSubmissions);
    }

    public Optional<Marathon> findById(String id) {
        return this.marathonPersistencePort.findById(id);
    }

    public Optional<MarathonStats> getStats(String id) {
        return this.marathonPersistencePort.findStatsById(id);
    }

    public Marathon create(final Marathon marathon) {
        marathon.setDefaultSetupTime(Duration.ofMinutes(15));
        marathon.setStartDate(marathon.getStartDate().withSecond(0));
        marathon.setEndDate(marathon.getEndDate().withSecond(0));

        return this.marathonPersistencePort.save(marathon);
    }

    public boolean exists(final String id) {
        return this.marathonPersistencePort.existsById(id);
    }

    public void update(final String id, final Marathon patch) throws NotFoundException {
        final var optionalMarathon = this.marathonPersistencePort.findById(id);

        if (optionalMarathon.isEmpty()) {
            throw new NotFoundException("Marathon not found");
        }

        final var oldMarathon = optionalMarathon.get();
        final boolean markedSelectionDone = !oldMarathon.isSelectionDone() && patch.isSelectionDone();

        patch.setStartDate(patch.getStartDate().withSecond(0));
        patch.setEndDate(patch.getEndDate().withSecond(0));

        if (markedSelectionDone) {
            // send accepted submissions
            if (patch.isAnnounceAcceptedSubmissions() && patch.hasWebhook()) {
                try {
                    this.webhookService.sendSelectionDoneEvent(
                        patch.getWebhook(),
                        this.selectionService.findAllByMarathonId(patch.getId())
                    );
                } catch (IOException e) {
                    Sentry.captureException(e);
                    LoggerFactory.getLogger(MarathonService.class).error("Sending selection done event failed", e);
                }
            }
        }

        if (patch.isScheduleDone()) {
            final Schedule schedule = this.scheduleService.findByMarathon(patch.getId());
            this.scheduleService.computeEndDate(patch, schedule);
            this.selectionService.rejectTodos(patch.getId());
            patch.setSelectionDone(true);
            patch.setCanEditSubmissions(false);
        }

        if (patch.isSubmissionsOpen()) {
            patch.setCanEditSubmissions(true);
        }

        if (patch.getSubmissionsStartDate() != null && patch.getSubmissionsEndDate() != null) {
            patch.setSubmissionsStartDate(patch.getSubmissionsStartDate().withSecond(0));
            patch.setSubmissionsEndDate(patch.getSubmissionsEndDate().withSecond(0));
            this.eventSchedulerService.scheduleSubmissions(patch);
        } else {
            this.eventSchedulerService.unscheduleSubmissions(patch);
        }

        // TODO: do this in the adapter
//        marathon.getQuestions().forEach(question -> question.setMarathon(marathon));
        this.marathonPersistencePort.save(patch);
    }

    public void delete(final String marathonId) throws NotFoundException {
        this.marathonPersistencePort.findById(marathonId).ifPresent((marathon) -> {
            this.eventSchedulerService.unscheduleSubmissions(marathon);
            this.incentiveService.deleteByMarathon(marathonId);
            this.scheduleService.deleteByMarathon(marathonId);
            this.submissionService.deleteByMarathon(marathon.getId());

            this.marathonPersistencePort.delete(marathon);
        });
    }

    public List<Marathon> findNext() {
        return this.marathonPersistencePort.findNextUp();
    }

    public List<Marathon> findSubmitsOpen() {
        return this.marathonPersistencePort.findSubmissionsOpen();
    }

    public List<Marathon> findLive() {
        return this.marathonPersistencePort.findLive();
    }

    public List<Marathon> findActiveMarathonsIModerate() {
        final var user = this.securityPort.getAuthenticatedUserId();

        return this.marathonPersistencePort.findActiveModeratedBy(user);
    }

    // This gets called inside the user service, no need for that
    @Deprecated(forRemoval = true)
    public List<Marathon> findAllMarathonsIModerate(int user) {
        return this.marathonPersistencePort.findAllModeratedBy(user);
    }

    public List<Marathon> findMarathonsForDates(
        final ZonedDateTime start, final ZonedDateTime end, final String zoneId
    ) {
        return this.marathonPersistencePort.findBetween(
            start.withZoneSameInstant(ZoneId.of(zoneId)),
            end.withZoneSameInstant(ZoneId.of(zoneId)).plusDays(1L)
        );
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void clearDonationExtraData() {
        final List<Marathon> marathons =
            this.marathonPersistencePort.findNotClearedBefore(
                ZonedDateTime.now().minusMonths(1L)
            );
        marathons.forEach(marathon -> {
            // TODO: clear donation extra data when we have donations again
            this.marathonPersistencePort.clear(marathon);
        });
    }
}
