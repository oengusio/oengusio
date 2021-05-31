package app.oengus.spring.security;

import app.oengus.entity.model.Marathon;
import app.oengus.entity.model.User;
import app.oengus.service.MarathonService;
import app.oengus.service.UserService;
import app.oengus.spring.model.Role;
import javassist.NotFoundException;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.core.Authentication;

import java.time.ZonedDateTime;
import java.util.Objects;

public class CustomMethodSecurityExpressionRoot extends SecurityExpressionRoot
    implements MethodSecurityExpressionOperations {

    private final MarathonService marathonService;
    private final UserService userService;
    private Object filterObject;
    private Object returnObject;

    public CustomMethodSecurityExpressionRoot(final Authentication authentication,
                                              final MarathonService marathonService,
                                              final UserService userService) {
        super(authentication);
        this.marathonService = marathonService;
        this.userService = userService;
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
        return user != null && user.getRoles().contains(Role.ROLE_BANNED);
    }

    public boolean isMarathonArchived(final String id) throws NotFoundException {
        final Marathon marathon = this.marathonService.findOne(id);
        return marathon.getEndDate().plusHours(1).isBefore(ZonedDateTime.now());
    }

    public boolean canUpdateMarathon(final String id) throws NotFoundException {
        final User user = this.getUser();
        if (user == null) {
            return false;
        }
        if (this.isAdmin()) {
            return true;
        }
        final Marathon marathon = this.marathonService.findOne(id);
        return (marathon.getCreator().getId() == user.getId() ||
            marathon.getModerators().stream().anyMatch(u -> u.getId() == user.getId())) &&
            ZonedDateTime.now().isBefore(marathon.getEndDate());
    }

    public boolean isSelectionDone(final String id) throws NotFoundException {
        final Marathon marathon = this.marathonService.findOne(id);
        return marathon.isSelectionDone();
    }

    public boolean isScheduleDone(final String id) throws NotFoundException {
        final Marathon marathon = this.marathonService.findOne(id);
        return marathon.isScheduleDone();
    }

    public boolean areSubmissionsOpen(final String id) throws NotFoundException {
        final Marathon marathon = this.marathonService.findOne(id);

        return marathon.isCanEditSubmissions() && marathon.isSubmitsOpen() &&
            ZonedDateTime.now().isBefore(marathon.getEndDate());
    }

    public User getUser() {
        final Object principal = this.getPrincipal();
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
