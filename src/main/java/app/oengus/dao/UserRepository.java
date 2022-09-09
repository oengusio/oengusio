package app.oengus.dao;

import app.oengus.entity.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {

    User findByDiscordId(String discordId);

    User findByTwitchId(String twitchId);

    User findByTwitterId(String twitterId);

    User findByPatreonId(String patreonId);

    User findByUsername(String username);

    boolean existsByUsernameIgnoreCase(String username);

    boolean existsByUsernameJapanese(String username);

    boolean existsByDiscordId(String discordId);

    boolean existsByTwitchId(String twitchId);

    List<User> findByUsernameContainingIgnoreCaseAndEnabledTrue(String username);

}
