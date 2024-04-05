package app.oengus.application.port.security;

import app.oengus.domain.OengusUser;

public interface UserSecurityPort {
    int getAuthenticatedUserId();

    OengusUser getAuthenticatedUser();
}
