package app.oengus.adapter.security;

import app.oengus.adapter.security.dto.UserDetailsDto;
import app.oengus.application.UserService;
import app.oengus.application.port.security.UserSecurityPort;
import app.oengus.domain.OengusUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserSecurityAdapter implements UserSecurityPort {
    private final UserService userService;

    @Override
    public int getAuthenticatedUserId() {
        return this.getAuthenticatedUserDetails().id();
    }

    @Override
    public OengusUser getAuthenticatedUser() {
        return this.userService.getById(this.getAuthenticatedUserId());
    }

    private UserDetailsDto getAuthenticatedUserDetails() {
        return (UserDetailsDto) this.getAuthentication().getPrincipal();
    }

    private Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }
}
