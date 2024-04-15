package app.oengus.service;

import app.oengus.application.port.persistence.MarathonPersistencePort;
import app.oengus.domain.marathon.Marathon;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class EventSchedulerService {
    private final TaskScheduler taskScheduler;
    private final MarathonPersistencePort marathonPersistencePort;
    private final Map<String, ScheduledFuture<?>> scheduledEvents = new ConcurrentHashMap<>();

    public void scheduleSubmissions(final Marathon marathon) {
        this.unscheduleSubmissions(marathon);

        if (marathon.getSubmissionsStartDate().isAfter(ZonedDateTime.now())) {
            this.scheduledEvents.put(marathon.getId() + "-start",
                this.taskScheduler.schedule(
                    () -> this.marathonPersistencePort.markSubmissionsOpen(marathon),
                    Instant.from(marathon.getSubmissionsStartDate())
                )
            );
        }

        if (marathon.getSubmissionsEndDate().isAfter(ZonedDateTime.now())) {
            this.scheduledEvents.put(marathon.getId() + "-end",
                this.taskScheduler.schedule(
                    () -> this.marathonPersistencePort.markSubmissionsClosed(marathon),
                    Instant.from(marathon.getSubmissionsEndDate())
                )
            );
        }
    }

    public void unscheduleSubmissions(final Marathon marathon) {
        final ScheduledFuture<?> start = this.scheduledEvents.get(marathon.getId() + "-start");
        final ScheduledFuture<?> end = this.scheduledEvents.get(marathon.getId() + "-end");

        if (start != null) {
            start.cancel(true);
            this.scheduledEvents.remove(marathon.getId() + "-start");
        }
        if (end != null) {
            end.cancel(true);
            this.scheduledEvents.remove(marathon.getId() + "-end");
        }
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void cleanup() {
        this.scheduledEvents.entrySet().removeIf(entry -> entry.getValue().getDelay(TimeUnit.SECONDS) < 0);
    }

}
