package app.oengus.mock.adapter.jpa;

import app.oengus.application.port.persistence.UserPersistencePort;
import app.oengus.domain.OengusUser;
import app.oengus.domain.Role;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Profile("test")
@Component
@RequiredArgsConstructor
public class MockUserPersistenceAdapter implements UserPersistencePort {
    private final Map<Integer, OengusUser> fakeDb = new HashMap<>();

    @Nullable
    @Override
    public OengusUser getById(int id) {
        return this.findById(id).orElse(null);
    }

    @Override
    public Optional<OengusUser> findById(int id) {
        return Optional.ofNullable(this.fakeDb.get(id));
    }

    @Override
    public List<OengusUser> findAllById(List<Integer> ids) {
        return List.of();
    }

    @Override
    public Optional<OengusUser> findByUsername(String username) {
        return this.fakeDb.values().stream().filter((u) -> u.getUsername().equals(username)).findFirst();
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
        this.fakeDb.put(user.getId(), user);

        return user;
    }

    @Override
    public boolean existsByUsername(String name) {
        return this.fakeDb.values().stream().anyMatch((u) -> u.getUsername().equals(name));
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
