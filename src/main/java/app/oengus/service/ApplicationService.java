package app.oengus.service;

import app.oengus.dao.ApplicationRepository;
import app.oengus.entity.constants.ApplicationStatus;
import app.oengus.entity.dto.ApplicationDto;
import app.oengus.entity.model.Application;
import app.oengus.entity.model.Team;
import app.oengus.entity.model.User;
import app.oengus.entity.model.api.ApplicationAuditlog;
import app.oengus.helper.BeanHelper;
import javassist.NotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
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

    public Application createApplication(int teamId, int userId, ApplicationDto data) {
        final Team team = new Team();
        team.setId(teamId);

        final User user = new User();
        user.setId(userId);

        final Application application = new Application();

        BeanHelper.copyProperties(data, application, "status");

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

        BeanHelper.copyProperties(application, applicationPatch, "status");
        application.setUpdatedAt(LocalDateTime.now());

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
