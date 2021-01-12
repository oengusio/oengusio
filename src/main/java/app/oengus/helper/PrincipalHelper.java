package app.oengus.helper;

import app.oengus.entity.model.User;
import app.oengus.exception.OengusBusinessException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.security.Principal;

public class PrincipalHelper {

	public static User getUserFromPrincipal(final Principal principal) {
	    if (principal == null) {
	        throw new OengusBusinessException("MISSING_USER");
        }

        return (User) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
	}

	public static User getCurrentUser() {
		if (SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof User) {
			return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		}
		return null;
	}

}
