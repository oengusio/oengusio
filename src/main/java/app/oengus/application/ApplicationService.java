package app.oengus.application;

import app.oengus.application.port.persistence.ApplicationPersistencePort;
import app.oengus.application.port.security.UserSecurityPort;
import app.oengus.domain.OengusUser;
import app.oengus.domain.volunteering.Application;
import app.oengus.domain.volunteering.ApplicationStatus;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ApplicationService {
    private final ApplicationPersistencePort applicationPersistencePort;
    private final UserSecurityPort securityPort;

    public List<Application> getByTeam(int teamId) {
        return this.applicationPersistencePort.findByTeam(teamId);
    }

    public Application getByTeamAndUser(int teamId, int userId) throws NotFoundException {
        return this.applicationPersistencePort.findByTeamAndUser(teamId, userId).orElseThrow(
            () -> new NotFoundException("User did not submit application for this team")
        );
    }

    public Application createApplication(int teamId, int userId, Application application) {
        final var user = this.securityPort.getAuthenticatedUser();

        application.setAuditLogs(new ArrayList<>());
        application.setStatus(ApplicationStatus.PENDING);
        application.setCreatedAt(LocalDateTime.now());
        application.setUpdatedAt(LocalDateTime.now());

        final var savedApplication = this.applicationPersistencePort.save(application);

        this.logStatusChange(savedApplication, user, savedApplication.getStatus());

        return savedApplication;
    }

    public Application save(Application application) throws NotFoundException {
        final var updatedBy = this.securityPort.getAuthenticatedUser();

        return this.save(application, updatedBy);
    }

    public Application save(Application application, OengusUser updatedBy) throws NotFoundException {
        final var oldApplication = this.getByTeamAndUser(
            application.getTeamId(),
            application.getUserId()
        );

        if (application.getStatus() != null && oldApplication.getStatus() != application.getStatus()) {
            this.logStatusChange(
                application,
                updatedBy,
                application.getStatus()
            );
        }

        application.setUpdatedAt(LocalDateTime.now());

        // this is absolute bullshit, why would I have to ignore the availabilities
        // it just does not make sense, this should just be copied over normally
        // but no, spring decided that it would be better if I were to ignore them when copying
        // and manually copy them over later on
        // 3 hours wasted that I never will get back ffs
//        application.setAvailabilities(applicationPatch.getAvailabilities());

        application.getAvailabilities().forEach((availability) -> {
            availability.setFrom(availability.getFrom().withSecond(0));
            availability.setTo(availability.getTo().withSecond(0));
        });

        return this.applicationPersistencePort.save(application);
    }

    public void changeApplicationStatus(int teamId, int userId, ApplicationStatus newStatus) throws NotFoundException {
        final var application = this.getByTeamAndUser(teamId, userId);

        this.applicationPersistencePort.updateStatus(
            application.getId(),
            this.securityPort.getAuthenticatedUserId(),
            newStatus
        );
    }

    public void logStatusChange(Application application, OengusUser updatedBy, ApplicationStatus newStatus) {
        this.applicationPersistencePort.logStatusChange(
            application.getId(),
            updatedBy.getId(),
            newStatus
        );
    }
}
