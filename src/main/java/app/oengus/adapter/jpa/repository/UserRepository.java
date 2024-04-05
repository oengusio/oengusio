package app.oengus.adapter.jpa.repository;

import app.oengus.entity.model.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Integer> {

    Optional<User> findByDiscordId(String discordId);

    Optional<User> findByTwitchId(String twitchId);

    User findByTwitterId(String twitterId);

    Optional<User> findByPatreonId(String patreonId);

    Optional<User> findByMail(String email);

    Optional<User> findByUsernameIgnoreCase(String username);

    boolean existsByUsernameIgnoreCase(String username);

    boolean existsByDiscordId(String discordId);

    boolean existsByTwitchId(String twitchId);

    List<User> findByUsernameContainingIgnoreCaseAndEnabledTrue(String username);

}
