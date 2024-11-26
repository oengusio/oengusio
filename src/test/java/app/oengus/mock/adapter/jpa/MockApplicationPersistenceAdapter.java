package app.oengus.mock.adapter.jpa;

import app.oengus.application.port.persistence.ApplicationPersistencePort;
import app.oengus.domain.volunteering.Application;
import app.oengus.domain.volunteering.ApplicationStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@Profile("test")
@RequiredArgsConstructor
public class MockApplicationPersistenceAdapter implements ApplicationPersistencePort {
    @Override
    public List<Application> findByTeam(int teamId) {
        return List.of();
    }

    @Override
    public Optional<Application> findByTeamAndUser(int teamId, int userId) {
        return Optional.empty();
    }

    @Override
    public Application save(Application application) {
        return null;
    }

    @Override
    public void updateStatus(int applicationId, int userId, ApplicationStatus newStatus) {

    }

    @Override
    public void logStatusChange(int applicationId, int userId, ApplicationStatus newStatus) {

    }
}
