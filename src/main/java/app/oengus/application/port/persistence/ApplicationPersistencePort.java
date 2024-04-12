package app.oengus.application.port.persistence;

import app.oengus.domain.volunteering.Application;
import app.oengus.domain.volunteering.ApplicationStatus;

import java.util.List;
import java.util.Optional;

public interface ApplicationPersistencePort {
    List<Application> findByTeam(int teamId);

    Optional<Application> findByTeamAndUser(int teamId, int userId);

    Application save(Application application);

    void updateStatus(int applicationId, int userId, ApplicationStatus newStatus);

}
