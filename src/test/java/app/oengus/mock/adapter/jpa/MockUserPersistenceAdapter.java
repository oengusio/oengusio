package app.oengus.mock.adapter.jpa;

import app.oengus.application.port.persistence.UserPersistencePort;
import app.oengus.domain.OengusUser;
import app.oengus.domain.Role;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Profile("test")
@Component
@RequiredArgsConstructor
public class MockUserPersistenceAdapter implements UserPersistencePort {
    @Nullable
    @Override
    public OengusUser getById(int id) {
        return null;
    }

    @Override
    public Optional<OengusUser> findById(int id) {
        return Optional.empty();
    }

    @Override
    public List<OengusUser> findAllById(List<Integer> ids) {
        return List.of();
    }

    @Override
    public Optional<OengusUser> findByUsername(String username) {
        return Optional.empty();
    }

    @Override
    public List<OengusUser> findEnabledByUsername(String username) {
        return List.of();
    }

    @Override
    public Optional<OengusUser> findByEmail(String email) {
        return Optional.empty();
    }

    @Override
    public OengusUser save(OengusUser user) {
        return null;
    }

    @Override
    public boolean existsByUsername(String name) {
        return false;
    }

    @Override
    public Optional<OengusUser> findByDiscordId(String discordId) {
        return Optional.empty();
    }

    @Override
    public Optional<OengusUser> findByTwitchId(String twitchId) {
        return Optional.empty();
    }

    @Override
    public Optional<OengusUser> findByPatreonId(String patreonId) {
        return Optional.empty();
    }

    @Override
    public void addRole(int userId, Role role) {

    }

    @Override
    public void removeRole(int userId, Role role) {

    }

    @Override
    public void deleteAll() {

    }
}
