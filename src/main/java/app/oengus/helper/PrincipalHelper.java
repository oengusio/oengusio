package app.oengus.helper;

import app.oengus.entity.model.User;
import app.oengus.exception.OengusBusinessException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.annotation.Nullable;
import java.security.Principal;

public class PrincipalHelper {

    @Nullable
    public static User getNullableUserFromPrincipal(@Nullable final Principal principal) {
        if (principal == null) {
            return null;
        }

        return (User) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
    }

	public static User getUserFromPrincipal(final Principal principal) {
	    if (principal == null) {
	        throw new OengusBusinessException("MISSING_USER");
        }

        return (User) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
	}

	public static User getCurrentUser() {
        final Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof User) {
			return (User) principal;
		}

		return null;
	}

}
