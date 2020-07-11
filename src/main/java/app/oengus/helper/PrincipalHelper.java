package app.oengus.helper;

import app.oengus.entity.model.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.security.Principal;

public class PrincipalHelper {

	public static User getUserFromPrincipal(final Principal principal) {
		if (principal != null) {
			return (User) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
		} else {
			return null;
		}
	}

	public static User getCurrentUser() {
		if (SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof User) {
			return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		}
		return null;
	}

}
