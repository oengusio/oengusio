package app.oengus.spring.security;

import app.oengus.entity.model.Marathon;
import app.oengus.entity.model.User;
import app.oengus.service.MarathonService;
import app.oengus.spring.model.Role;
import javassist.NotFoundException;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.core.Authentication;

import java.time.ZonedDateTime;
import java.util.Objects;

public class CustomMethodSecurityExpressionRoot extends SecurityExpressionRoot
		implements MethodSecurityExpressionOperations {

	private Object filterObject;
	private Object returnObject;

	private final MarathonService marathonService;

	public CustomMethodSecurityExpressionRoot(final Authentication authentication,
                                              final MarathonService marathonService) {
		super(authentication);
		this.marathonService = marathonService;
    }

	public boolean isSelf(final Integer id) {
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
		return (marathon.getCreator().getId().equals(user.getId()) ||
				marathon.getModerators().stream().anyMatch(u -> u.getId().equals(user.getId()))) &&
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
		return marathon.isCanEditSubmissions() &&
				ZonedDateTime.now().isBefore(marathon.getEndDate());
	}

	public User getUser() {
		final Object principal = this.getPrincipal();
		if (principal instanceof User) {
		    return (User) principal;
        }

		return null;
	}

	@Override
	public Object getFilterObject() {
		return this.filterObject;
	}

	@Override
	public Object getReturnObject() {
		return this.returnObject;
	}

	@Override
	public Object getThis() {
		return this;
	}

	@Override
	public void setFilterObject(final Object obj) {
		this.filterObject = obj;
	}

	@Override
	public void setReturnObject(final Object obj) {
		this.returnObject = obj;
	}
}
