package app.oengus.application;

import app.oengus.application.port.persistence.CategoryPersistencePort;
import app.oengus.application.port.persistence.MarathonPersistencePort;
import app.oengus.application.port.persistence.SubmissionPersistencePort;
import app.oengus.application.port.security.UserSecurityPort;
import app.oengus.domain.OengusUser;
import app.oengus.domain.exception.MarathonNotFoundException;
import app.oengus.domain.marathon.Marathon;
import app.oengus.domain.marathon.MarathonStats;
import app.oengus.domain.marathon.Question;
import app.oengus.domain.submission.Selection;
import app.oengus.domain.webhook.CategoryAndUserId;
import app.oengus.domain.webhook.WebhookSelectionDone;
import io.sentry.Sentry;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MarathonService {
    private final MarathonPersistencePort marathonPersistencePort;
    private final SubmissionService submissionService;
    private final SubmissionPersistencePort submissionPersistencePort;
    private final ScheduleService scheduleService;
    private final CategoryPersistencePort categoryPersistencePort;
    private final IncentiveService incentiveService;
    private final SelectionService selectionService;
    private final OengusWebhookService webhookService;
    private final UserSecurityPort securityPort;

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

    public List<OengusUser> findModerators(final String id) {
        return this.marathonPersistencePort.findById(id)
            .orElseThrow(MarathonNotFoundException::new)
            .getModerators();
    }

    public void setModerators(final String id, final List<OengusUser> moderators) {
        final var marathon = this.marathonPersistencePort.findById(id)
            .orElseThrow(MarathonNotFoundException::new);

        marathon.setModerators(moderators);

        this.marathonPersistencePort.save(marathon);
    }

    public void removeModerator(final String marathonId, final int userId) {
        final var marathon = this.marathonPersistencePort.findById(marathonId)
            .orElseThrow(MarathonNotFoundException::new);

        final var mods = marathon.getModerators();
        var modRemoved = false;

        for (final var mod : mods) {
            if (mod.getId() == userId) {
                modRemoved = mods.remove(mod);
                break;
            }
        }

        if (modRemoved) {
            this.marathonPersistencePort.save(marathon);
        }
    }

    public List<Question> findQuestions(final String id) {
        return this.marathonPersistencePort.findById(id)
            .orElseThrow(MarathonNotFoundException::new)
            .getQuestions();
    }

    public void updateQuestions(final String marathonId, final List<Question> questions) {
        final var marathon = this.marathonPersistencePort.findById(marathonId)
            .orElseThrow(MarathonNotFoundException::new);

        marathon.setQuestions(questions);

        this.marathonPersistencePort.save(marathon);
    }

    public void removeQuestion(final String marathonId, final int questionId) {
        final var marathon = this.marathonPersistencePort.findById(marathonId)
            .orElseThrow(MarathonNotFoundException::new);

        final var questions = marathon.getQuestions();
        var questionRemoved = false;

        for (final var question : questions) {
            if (question.getId() == questionId) {
                questionRemoved = questions.remove(question);
                break;
            }
        }

        if (questionRemoved) {
            this.marathonPersistencePort.save(marathon);
        }
    }

    public Marathon update(final String id, final Marathon patch) {
        final var oldMarathon = this.marathonPersistencePort.findById(id)
            .orElseThrow(MarathonNotFoundException::new);
        final boolean markedSelectionDone = !oldMarathon.isSelectionDone() && patch.isSelectionDone();
        final boolean markedScheduleDone = !oldMarathon.isScheduleDone() && patch.isScheduleDone();

        patch.setStartDate(patch.getStartDate().withSecond(0));
        patch.setEndDate(patch.getEndDate().withSecond(0));

        patch.getQuestions().forEach((question) -> {
            question.setMarathonId(oldMarathon.getId());
        });

        if (markedSelectionDone) {
            // send accepted submissions
            if (patch.isAnnounceAcceptedSubmissions() && patch.hasWebhook()) {
                try {
                    final var selections = this.selectionService.findAllByMarathonId(patch.getId());

                    // TODO: optimize this to a single query, I want to see if I can fetch the category with the userid
                    final var categoryIds = selections.stream().map(Selection::getCategoryId).toList();
                    final var categories = this.categoryPersistencePort.findAllById(categoryIds);
                    final Map<Integer, CategoryAndUserId> categoryMap = new HashMap<>();

                    for (final var category : categories) {
                        final var userId = this.submissionPersistencePort.getToplevelByGamId(category.getGameId()).getUser().getId();
                        categoryMap.put(category.getId(), new CategoryAndUserId(category, userId));
                    }

                    final var parsedSelections = selections
                        .stream()
                        .map((sel) -> {
                            final var data = categoryMap.get(sel.getCategoryId());
                            final var hookData = new WebhookSelectionDone(
                                sel.getId(), sel.getCategoryId(),
                                data.category(), data.userId()
                            );

                            hookData.setMarathonId(sel.getMarathonId());
                            hookData.setStatus(sel.getStatus());

                            return hookData;
                        })
                        .toList();

                    this.webhookService.sendSelectionDoneEvent(patch.getWebhook(), parsedSelections);
                } catch (IOException e) {
                    Sentry.captureException(e);
                    LoggerFactory.getLogger(MarathonService.class).error("Sending selection done event failed", e);
                }
            }
        }

        // TODO: handle multi-schedules properly
        if (patch.isScheduleDone()) {
            // TODO: check if any schedule info is published
            final var schedules = this.scheduleService.findAllInfoByMarathon(patch.getId());

            if (schedules.isEmpty()) {
                patch.setScheduleDone(false);
            } else {
                final var scheduleDate = this.scheduleService.findByScheduleId(patch.getId(), schedules.get(0).getId(), false).orElseThrow();
                // Calculate it using the proper schedule
                this.scheduleService.computeEndDate(patch, scheduleDate);
                this.selectionService.rejectTodos(patch.getId());

                patch.setSelectionDone(true);
                patch.setCanEditSubmissions(false);
            }
        }

        // TODO: moving this above the previous check prevents submissions open and schedule published at the same time.
        if (patch.isSubmissionsOpen()) {
            patch.setCanEditSubmissions(true);
        }

        if (patch.getSubmissionsStartDate() != null && patch.getSubmissionsEndDate() != null) {
            patch.setSubmissionsStartDate(patch.getSubmissionsStartDate().withSecond(0));
            patch.setSubmissionsEndDate(patch.getSubmissionsEndDate().withSecond(0));
        }

        return this.marathonPersistencePort.save(patch);
    }

    public void delete(final String marathonId) throws NotFoundException {
        this.marathonPersistencePort.findById(marathonId).ifPresent((marathon) -> {
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

    public Optional<OengusUser> findCreatorById(final String marathonId) {
        return this.marathonPersistencePort.findCreatorById(marathonId);
    }

    public List<Marathon> findMarathonsForDates(
        final ZonedDateTime start, final ZonedDateTime end, final String zoneId
    ) {
        return this.marathonPersistencePort.findBetween(
            start.withZoneSameInstant(ZoneId.of(zoneId)),
            end.withZoneSameInstant(ZoneId.of(zoneId)).plusDays(1L)
        );
    }

    // Disabled, causes issues
    /*@Scheduled(cron = "0 0 0 * * *")
    public void clearDonationExtraData() {
        final List<Marathon> marathons =
            this.marathonPersistencePort.findNotClearedBefore(
                ZonedDateTime.now().minusMonths(1L)
            );
        marathons.forEach(marathon -> {
            // TODO: clear donation extra data when we have donations again
            // TODO: move this to the microservice when we have donations again
            this.marathonPersistencePort.clear(marathon);
        });
    }*/
}
