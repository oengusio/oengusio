package app.oengus.adapter.security;

import app.oengus.adapter.security.dto.UserDetailsDto;
import app.oengus.application.UserLookupService;
import app.oengus.application.UserService;
import app.oengus.application.port.security.UserSecurityPort;
import app.oengus.domain.OengusUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;

@Service
@RequiredArgsConstructor
public class UserSecurityAdapter implements UserSecurityPort {
    private final UserLookupService userService;

    @Override
    public int getAuthenticatedUserId() {
        final var details = this.getAuthenticatedUserDetails();

        if (details == null) {
            return -1;
        }

        return details.id();
    }

    @Nullable
    @Override
    public OengusUser getAuthenticatedUser() {
        final var id = this.getAuthenticatedUserId();

        if (id > -1) {
            return this.userService.getById(id);
        }

        return null;
    }

    @Nullable
    private UserDetailsDto getAuthenticatedUserDetails() {
        final var auth = this.getAuthentication();

        if (!auth.isAuthenticated()) {
            return null;
        }

        return (UserDetailsDto) auth.getPrincipal();
    }

    private Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }
}
