package app.oengus.application;

import app.oengus.application.port.persistence.UserPersistencePort;
import app.oengus.domain.OengusUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserLookupService {
    private final UserPersistencePort userPersistencePort;

    @Nullable
    public OengusUser getById(int id) {
        return this.userPersistencePort.getById(id);
    }

    public Optional<OengusUser> findById(int id) {
        return this.userPersistencePort.findById(id);
    }

    public Optional<OengusUser> findByUsername(final String username) {
        return this.userPersistencePort.findByUsername(username);
    }

    public List<OengusUser> searchByUsername(String username) {
        return this.userPersistencePort.findEnabledByUsername(username);
    }

    public Optional<OengusUser> findByEmail(final String email) {
        return this.userPersistencePort.findByEmail(email);
    }
}
