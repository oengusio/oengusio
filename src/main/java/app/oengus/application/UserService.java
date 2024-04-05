package app.oengus.application;

import app.oengus.application.port.persistence.UserPersistencePort;
import app.oengus.domain.OengusUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserPersistencePort userPersistencePort;

    @Nullable
    public OengusUser getById(int id) {
        return this.userPersistencePort.getById(id);
    }

    public Optional<OengusUser> findByUsername(final String username) {
        return this.userPersistencePort.findByUsername(username);
    }

    public Optional<OengusUser> findByEmail(final String email) {
        return this.userPersistencePort.findByEmail(email);
    }

    public boolean existsByUsername(String name) {
        return this.userPersistencePort.existsByUsername(name);
    }

    public OengusUser save(final OengusUser user) {
        return this.userPersistencePort.save(user);
    }
}
