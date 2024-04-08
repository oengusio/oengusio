package app.oengus.adapter.jpa;

import app.oengus.adapter.jpa.entity.User;
import app.oengus.adapter.jpa.mapper.UserMapper;
import app.oengus.adapter.jpa.repository.UserRepository;
import app.oengus.application.port.persistence.UserPersistencePort;
import app.oengus.domain.OengusUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserPersistenceAdapter implements UserPersistencePort {
    private final UserRepository userRepository;
    private final UserMapper mapper;

    @Nullable
    @Override
    public OengusUser getById(int id) {
        return this.userRepository.findById(id)
            .map(this.mapper::toDomain)
            .orElse(null);
    }

    @Override
    public List<OengusUser> findAllById(List<Integer> ids) {
        return ((List<User>) this.userRepository.findAllById(ids))
            .stream()
            .map(this.mapper::toDomain)
            .toList();
    }

    @Override
    public Optional<OengusUser> findByUsername(String username) {
        return this.userRepository.findByUsernameIgnoreCase(username)
            .map(this.mapper::toDomain);
    }

    @Override
    public List<OengusUser> findEnabledByUsername(String username) {
        return this.userRepository.findByUsernameContainingIgnoreCaseAndEnabledTrue(username)
            .stream()
            .map(this.mapper::toDomain)
            .toList();
    }

    @Override
    public Optional<OengusUser> findByEmail(String email) {
        return this.userRepository.findByMail(email)
            .map(this.mapper::toDomain);
    }

    @Override
    public OengusUser save(OengusUser user) {
        final var internalUser = this.mapper.fromDomain(user);
        final var savedUser = this.userRepository.save(internalUser);

        return this.mapper.toDomain(savedUser);
    }

    @Override
    public boolean existsByUsername(String name) {
        return this.userRepository.existsByUsernameIgnoreCase(name);
    }

    @Override
    public Optional<OengusUser> findByDiscordId(String discordId) {
        return this.userRepository.findByDiscordId(discordId)
            .map(this.mapper::toDomain);
    }

    @Override
    public Optional<OengusUser> findByTwitchId(String twitchId) {
        return this.userRepository.findByTwitchId(twitchId)
            .map(this.mapper::toDomain);
    }

    @Override
    public Optional<OengusUser> findByPatreonId(String patreonId) {
        return this.userRepository.findByPatreonId(patreonId)
            .map(this.mapper::toDomain);
    }
}
