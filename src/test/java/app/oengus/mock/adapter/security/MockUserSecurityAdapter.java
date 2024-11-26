package app.oengus.mock.adapter.security;

import app.oengus.application.port.security.UserSecurityPort;
import app.oengus.domain.OengusUser;
import org.jetbrains.annotations.Nullable;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("test")
public class MockUserSecurityAdapter implements UserSecurityPort {
    @Override
    public int getAuthenticatedUserId() {
        return -1;
    }

    @Nullable
    @Override
    public OengusUser getAuthenticatedUser() {
        return null;
    }
}
