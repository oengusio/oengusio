package app.oengus.service;

import app.oengus.dao.ApplicationRepository;
import app.oengus.entity.constants.ApplicationStatus;
import app.oengus.entity.dto.ApplicationDto;
import app.oengus.entity.model.Application;
import app.oengus.entity.model.Team;
import app.oengus.adapter.jpa.entity.User;
import app.oengus.entity.model.api.ApplicationAuditlog;
import app.oengus.helper.BeanHelper;
import javassist.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ApplicationService {
    private final ApplicationRepository applicationRepository;

    public ApplicationService(ApplicationRepository applicationRepository) {
        this.applicationRepository = applicationRepository;
    }

    public List<Application> getByTeam(int teamId) {
        final Team team = new Team();
        team.setId(teamId);

        return this.applicationRepository.findByTeam(team);
    }

    public Application getByTeamAndUser(int teamId, int userId) throws NotFoundException {
        final Team team = new Team();
        team.setId(teamId);

        final User user = new User();
        user.setId(userId);

        return this.applicationRepository.findByTeamAndUser(team, user).orElseThrow(
            () -> new NotFoundException("User did not submit application for this team")
        );
    }

    @Transactional
    public Application createApplication(int teamId, int userId, ApplicationDto data) {
        final Team team = new Team();
        team.setId(teamId);

        final User user = new User();
        user.setId(userId);

        final Application application = new Application();

        BeanHelper.copyProperties(data, application, "status");

        application.setAuditLogs(new ArrayList<>());
        application.setId(-1);
        application.setTeam(team);
        application.setUser(user);
        application.setStatus(ApplicationStatus.PENDING);
        application.setCreatedAt(LocalDateTime.now());
        application.setUpdatedAt(LocalDateTime.now());

        this.updateStatus(application, user, application.getStatus());

        return this.applicationRepository.save(application);
    }

    @Transactional
    public Application update(int teamId, int userId, User updatedBy, ApplicationDto applicationPatch) throws NotFoundException {
        return this.update(
            this.getByTeamAndUser(teamId, userId),
            updatedBy,
            applicationPatch
        );
    }

    @Transactional
    public Application update(Application application, User updatedBy, ApplicationDto applicationPatch) {
        if (applicationPatch.getStatus() != null && application.getStatus() != applicationPatch.getStatus()) {
            this.updateStatus(
                application,
                updatedBy,
                applicationPatch.getStatus()
            );
        }

        BeanHelper.copyProperties(application, applicationPatch, "status", "availabilities");
        application.setUpdatedAt(LocalDateTime.now());

        // this is absolute bullshit, why would I have to ignore the availabilities
        // it just does not make sense, this should just be copied over normally
        // but no, spring decided that it would be better if I were to ignore them when copying
        // and manually copy them over later on
        // 3 hours wasted that I never will get back ffs
        application.setAvailabilities(applicationPatch.getAvailabilities());

        application.getAvailabilities().forEach((availability) -> {
            availability.setFrom(availability.getFrom().withSecond(0));
            availability.setTo(availability.getTo().withSecond(0));
        });

        return this.applicationRepository.save(application);
    }

    public void updateStatus(Application application, User updatedBy, ApplicationStatus newStatus) {
        application.setStatus(newStatus);

        final ApplicationAuditlog log = new ApplicationAuditlog();
        log.setId(-1);
        log.setTimestamp(LocalDateTime.now());
        log.setApplication(application);
        log.setStatus(newStatus);
        log.setUser(updatedBy);

        application.getAuditLogs().add(log);
    }
}
