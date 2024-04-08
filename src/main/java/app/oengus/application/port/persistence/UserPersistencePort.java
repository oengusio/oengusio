package app.oengus.application.port.persistence;

import app.oengus.domain.OengusUser;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public interface UserPersistencePort {
    @Nullable
    OengusUser getById(final int id);

    List<OengusUser> findAllById(List<Integer> ids);

    Optional<OengusUser> findByUsername(final String username);

    List<OengusUser> findEnabledByUsername(final String username);

    Optional<OengusUser> findByEmail(final String email);

    OengusUser save(final OengusUser user);

    boolean existsByUsername(String name);

    Optional<OengusUser> findByDiscordId(final String discordId);

    Optional<OengusUser> findByTwitchId(final String twitchId);

    Optional<OengusUser> findByPatreonId(final String patreonId);
}