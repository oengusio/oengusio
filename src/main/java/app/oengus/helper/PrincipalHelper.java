package app.oengus.helper;

import app.oengus.adapter.jpa.entity.User;
import app.oengus.exception.OengusBusinessException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.annotation.Nullable;
import java.security.Principal;

// The principal is no longer a User object to avoid a lot of confusion.
// This is gonna cause errors during testing for sure.
@Deprecated(forRemoval = true)
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
