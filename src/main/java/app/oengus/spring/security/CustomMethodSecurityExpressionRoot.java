package app.oengus.spring.security;

import app.oengus.adapter.jpa.entity.MarathonEntity;
import app.oengus.entity.model.Team;
import app.oengus.adapter.jpa.entity.User;
import app.oengus.service.MarathonService;
import app.oengus.service.UserService;
import app.oengus.service.repository.TeamRepositoryService;
import app.oengus.spring.model.Role;
import javassist.NotFoundException;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.core.Authentication;

import javax.annotation.Nullable;
import java.time.ZonedDateTime;
import java.util.Objects;

public class CustomMethodSecurityExpressionRoot extends SecurityExpressionRoot
    implements MethodSecurityExpressionOperations {

    private final TeamRepositoryService teamRepositoryService;
    private final MarathonService marathonService;
    private final UserService userService;
    private Object filterObject;
    private Object returnObject;

    public CustomMethodSecurityExpressionRoot(final Authentication authentication,
                                              final MarathonService marathonService,
                                              final UserService userService,
                                              final TeamRepositoryService teamRepositoryService) {
        super(authentication);
        this.marathonService = marathonService;
        this.userService = userService;
        this.teamRepositoryService = teamRepositoryService;
    }

    public boolean isSelf(final int id) {
        final User user = this.getUser();

        return user != null && Objects.equals(user.getId(), id);
    }

    public boolean isAdmin() {
        final User user = this.getUser();

        return user != null && user.getRoles().contains(Role.ROLE_ADMIN);
    }

    public boolean isBanned() {
        final User user = this.getUser();

        if (user == null) {
            return true;
        }

        return user.getRoles().contains(Role.ROLE_BANNED);
    }

    public boolean isMarathonArchived(final String id) throws NotFoundException {
        final MarathonEntity marathon = this.marathonService.getById(id);
        return marathon.getEndDate().plusHours(1).isBefore(ZonedDateTime.now());
    }

    public boolean canHaveTeams() {
        return false;
    }

    public boolean canUpdateTeam(final int teamId) throws NotFoundException {
        final User user = this.getUser();

        if (user == null) {
            return false;
        }

        if (this.isAdmin()) {
            return true;
        }

        final Team team = this.teamRepositoryService.getById(teamId);
        final int uId = user.getId();

        return team.getLeaders().stream().anyMatch((u) -> u.getId() == uId) || this.isMarathonMod(team.getMarathon(), user);
    }

    public boolean applicationsOpen(final int teamId) throws NotFoundException {
        final Team team = this.teamRepositoryService.getById(teamId);

        return team.isApplicationsOpen();
    }

    /**
     * TODO: admin and mod are used interchangeably. Admins should have more power than mods
     */
    public boolean isMarathonAdmin(final String marathonId) throws NotFoundException {
        final User user = this.getUser();

        if (user == null) {
            return false;
        }

        if (this.isAdmin()) {
            return true;
        }

        final MarathonEntity marathon = this.marathonService.getById(marathonId);

        return Objects.equals(marathon.getCreator().getId(), user.getId());
    }

    public boolean isMarathonMod(final String marathonId) throws NotFoundException {
        final User user = this.getUser();

        if (user == null) {
            return false;
        }

        if (this.isAdmin()) {
            return true;
        }

        final MarathonEntity marathon = this.marathonService.getById(marathonId);

        return this.isMarathonMod(marathon, user);
    }

    public boolean canUpdateMarathon(final String id) throws NotFoundException {
        final User user = this.getUser();

        if (user == null) {
            return false;
        }

        if (this.isAdmin()) {
            return true;
        }

        final MarathonEntity marathon = this.marathonService.getById(id);
        return this.isMarathonMod(marathon, user) && ZonedDateTime.now().isBefore(marathon.getEndDate());
    }

    public boolean isSelectionDone(final String id) throws NotFoundException {
        final MarathonEntity marathon = this.marathonService.getById(id);
        return marathon.isSelectionDone();
    }

    public boolean isScheduleDone(final String id) throws NotFoundException {
        final MarathonEntity marathon = this.marathonService.getById(id);
        return marathon.isScheduleDone();
    }

    public boolean canEditSubmissions(final String marathonId) throws NotFoundException {
        final MarathonEntity marathon = this.marathonService.getById(marathonId);

        return marathon.isCanEditSubmissions();
    }

    public boolean areSubmissionsOpen(final String id) throws NotFoundException {
        final MarathonEntity marathon = this.marathonService.getById(id);

        return (marathon.isCanEditSubmissions() && marathon.isSubmitsOpen() &&
            ZonedDateTime.now().isBefore(marathon.getEndDate())) ||
            (marathon.getSubmissionsEndDate() != null && ZonedDateTime.now().isBefore(marathon.getSubmissionsEndDate()));
    }

    private boolean isMarathonMod(MarathonEntity marathon, User user) {
        final int uId = user.getId();

        return marathon.getCreator().getId() == uId ||
            marathon.getModerators().stream().anyMatch((u) -> u.getId() == uId);
    }

    @Nullable
    public User getUser() {
        if (this.isAnonymous()) {
            return null;
        }

        final Object principal = this.getPrincipal();
        // TODO: this is not a user model anymore
        //  Meaning this check will always fail
        if (principal instanceof final User tmp) {
            try {
                // fetch an up-to-date user to make sure we have the correct roles
                return this.userService.getUser(tmp.getId());
            } catch (NotFoundException ignored) {
                return null;
            }
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
}
