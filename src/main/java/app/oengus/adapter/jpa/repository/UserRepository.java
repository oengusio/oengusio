package app.oengus.adapter.jpa.repository;

import app.oengus.adapter.jpa.entity.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

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

    @Query("SELECT id > 0 FROM User WHERE id = :userId AND hashedPassword IS NOT NULL AND hashedPassword != ''")
    Optional<Boolean> hasPasswordById(@Param("userId") int userId);

    List<User> findByUsernameContainingIgnoreCaseAndEnabledTrue(String username);

}
