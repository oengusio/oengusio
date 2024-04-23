package app.oengus.application.port.security;

import app.oengus.domain.OengusUser;

import javax.annotation.Nullable;

public interface UserSecurityPort {
    /**
     * @return the id of the authenticated user, or -1 if no user is authenticated.
     */
    int getAuthenticatedUserId();

    @Nullable
    OengusUser getAuthenticatedUser();
}
