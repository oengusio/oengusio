package app.oengus.adapter.security;

import app.oengus.adapter.security.dto.UserDetailsDto;
import app.oengus.application.MarathonService;
import app.oengus.application.port.persistence.UserPersistencePort;
import app.oengus.domain.OengusUser;
import app.oengus.domain.marathon.Marathon;
import app.oengus.domain.Role;
import javassist.NotFoundException;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.core.Authentication;

import javax.annotation.Nullable;
import java.time.ZonedDateTime;
import java.util.Objects;

public class CustomMethodSecurityExpressionRoot extends SecurityExpressionRoot
    implements MethodSecurityExpressionOperations {

    private final MarathonService marathonService;
    private final UserPersistencePort userPersistencePort;
    private Object filterObject;
    private Object returnObject;

    public CustomMethodSecurityExpressionRoot(final Authentication authentication,
                                              final MarathonService marathonService,
                                              final UserPersistencePort userPersistencePort) {
        super(authentication);
        this.marathonService = marathonService;
        this.userPersistencePort = userPersistencePort;
    }

    public boolean isSelf(final int id) {
        final var user = this.getUser();

        return user != null && Objects.equals(user.getId(), id);
    }

    public boolean isAdmin() {
        final var user = this.getUser();

        return user != null && user.getRoles().contains(Role.ROLE_ADMIN);
    }

    public boolean isBanned() {
        final var user = this.getUser();

        if (user == null) {
            return true;
        }

        return user.getRoles().contains(Role.ROLE_BANNED);
    }

    public boolean isMarathonArchived(final String id) throws NotFoundException {
        final Marathon marathon = this.getMarathon(id);

        return marathon.getEndDate().plusHours(1).isBefore(ZonedDateTime.now());
    }

    public boolean canHaveTeams() {
        return false;
    }

    // TODO: re-implement when we have teams and applications
    public boolean canUpdateTeam(final int teamId) throws NotFoundException {
        /*final var user = this.getUser();

        if (user == null) {
            return false;
        }

        if (this.isAdmin()) {
            return true;
        }

        final Team team = this.teamRepositoryService.getById(teamId);
        final int uId = user.getId();

        return team.getLeaders().stream().anyMatch((u) -> u.getId() == uId) || this.isMarathonMod(team.getMarathon(), user);*/
        return false;
    }

    public boolean applicationsOpen(final int teamId) throws NotFoundException {
        /*final Team team = this.teamRepositoryService.getById(teamId);

        return team.isApplicationsOpen();*/
        return false;
    }

    /**
     * TODO: admin and mod are used interchangeably. Admins should have more power than mods
     */
    public boolean isMarathonAdmin(final String marathonId) throws NotFoundException {
        final var user = this.getUser();

        if (user == null) {
            return false;
        }

        if (this.isAdmin()) {
            return true;
        }

        final var marathon = this.getMarathon(marathonId);

        return Objects.equals(marathon.getCreatorId(), user.getId());
    }

    public boolean isMarathonMod(final String marathonId) throws NotFoundException {
        final var user = this.getUser();

        if (user == null) {
            return false;
        }

        if (this.isAdmin()) {
            return true;
        }

        final Marathon marathon = this.getMarathon(marathonId);

        return this.isMarathonMod(marathon, user);
    }

    public boolean canUpdateMarathon(final String id) throws NotFoundException {
        final var user = this.getUser();

        if (user == null) {
            return false;
        }

        if (this.isAdmin()) {
            return true;
        }

        final Marathon marathon = this.getMarathon(id);

        return this.isMarathonMod(marathon, user) && ZonedDateTime.now().isBefore(marathon.getEndDate());
    }

    public boolean isSelectionDone(final String id) throws NotFoundException {
        final Marathon marathon = this.getMarathon(id);

        return marathon.isSelectionDone();
    }

    public boolean isScheduleDone(final String id) throws NotFoundException {
        final Marathon marathon = this.getMarathon(id);

        return marathon.isScheduleDone();
    }

    public boolean canEditSubmissions(final String marathonId) throws NotFoundException {
        final Marathon marathon = this.getMarathon(marathonId);

        return marathon.isCanEditSubmissions();
    }

    public boolean areSubmissionsOpen(final String id) throws NotFoundException {
        final Marathon marathon = this.getMarathon(id);

        return (marathon.isCanEditSubmissions() && marathon.isSubmissionsOpen() &&
            ZonedDateTime.now().isBefore(marathon.getEndDate())) ||
            (marathon.getSubmissionsEndDate() != null && ZonedDateTime.now().isBefore(marathon.getSubmissionsEndDate()));
    }

    private boolean isMarathonMod(Marathon marathon, OengusUser user) {
        final int uId = user.getId();

        return marathon.getCreatorId() == uId ||
            marathon.getModerators().stream().anyMatch((u) -> u.getId() == uId);
    }

    @Nullable
    public OengusUser getUser() {
        if (this.isAnonymous()) {
            return null;
        }

        if (this.getPrincipal() instanceof final UserDetailsDto tmp) {
            // fetch an up-to-date user to make sure we have the correct roles
            return this.userPersistencePort.getById(tmp.id());
        }

        return null;
    }

    @Override
    public Object getFilterObject() {
        return this.filterObject;
    }

    @Override
    public void setFilterObject(final Object obj) {
        this.filterObject = obj;
    }

    @Override
    public Object getReturnObject() {
        return this.returnObject;
    }

    @Override
    public void setReturnObject(final Object obj) {
        this.returnObject = obj;
    }

    @Override
    public Object getThis() {
        return this;
    }

    private Marathon getMarathon(String marathonId) throws NotFoundException {
        return this.marathonService.findById(marathonId).orElseThrow(
            () -> new NotFoundException("Marathon not found")
        );
    }
}
