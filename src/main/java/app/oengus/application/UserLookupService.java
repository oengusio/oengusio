package app.oengus.application;

import app.oengus.application.port.persistence.UserPersistencePort;
import app.oengus.domain.OengusUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;

@Service
@RequiredArgsConstructor
public class UserLookupService {
    private final UserPersistencePort userPersistencePort;

    @Nullable
    public OengusUser getById(int id) {
        return this.userPersistencePort.getById(id);
    }
}
