package app.oengus.dao;

import app.oengus.entity.model.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserRepository extends CrudRepository<User, Integer> {

    User findByDiscordId(String discordId);

    User findByTwitchId(String twitchId);

    User findByTwitterId(String twitterId);

    User findByPatreonId(String patreonId);

    User findByUsername(String username);

    boolean existsByUsernameIgnoreCase(String username);

    boolean existsByDiscordId(String discordId);

    boolean existsByTwitchId(String twitchId);

    List<User> findByUsernameContainingIgnoreCaseAndEnabledTrue(String username);

}
