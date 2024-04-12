package app.oengus.adapter.jpa;

import app.oengus.adapter.jpa.entity.ApplicationEntry;
import app.oengus.adapter.jpa.entity.User;
import app.oengus.adapter.jpa.mapper.ApplicationEntryMapper;
import app.oengus.adapter.jpa.repository.ApplicationRepository;
import app.oengus.application.port.persistence.ApplicationPersistencePort;
import app.oengus.domain.volunteering.Application;
import app.oengus.domain.volunteering.ApplicationStatus;
import app.oengus.entity.model.Team;
import app.oengus.entity.model.api.ApplicationAuditlog;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ApplicationPersistenceAdapter implements ApplicationPersistencePort {
    private final ApplicationRepository repository;
    private final ApplicationEntryMapper mapper;

    @Override
    public List<Application> findByTeam(int teamId) {
        return this.repository.findByTeam(Team.ofId(teamId))
            .stream()
            .map(this.mapper::toDomain)
            .toList();
    }

    @Override
    public Optional<Application> findByTeamAndUser(int teamId, int userId) {
        return this.repository.findByTeamAndUser(Team.ofId(teamId), User.ofId(userId))
            .map(this.mapper::toDomain);
    }

    @Override
    public Application save(Application application) {
        final var entity = this.mapper.fromDomain(application);
        final var savedEntity = this.repository.save(entity);

        return this.mapper.toDomain(savedEntity);
    }

    @Override
    public void updateStatus(int applicationId, int userId, ApplicationStatus newStatus) {
        final var optionalApp = this.repository.findById(applicationId);

        if (optionalApp.isPresent()) {
            final var app = optionalApp.get();
            app.setStatus(newStatus);
            app.setUpdatedAt(LocalDateTime.now());

            logStatusChangeAndSave(app, userId, newStatus);
        }
    }

    @Override
    public void logStatusChange(int applicationId, int userId, ApplicationStatus newStatus) {
        final var optionalApp = this.repository.findById(applicationId);

        optionalApp.ifPresent(
            (applicationEntry) -> logStatusChangeAndSave(applicationEntry, userId, newStatus)
        );
    }

    private void logStatusChangeAndSave(ApplicationEntry application, int userId, ApplicationStatus newStatus) {
        final ApplicationAuditlog log = new ApplicationAuditlog();
        log.setId(-1);
        log.setTimestamp(LocalDateTime.now());
        log.setApplication(application);
        log.setStatus(newStatus);
        log.setUser(User.ofId(userId));

        application.getAuditLogs().add(log);

        this.repository.save(application);
    }
}
