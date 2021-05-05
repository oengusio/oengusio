package app.oengus.dao;

import app.oengus.entity.model.User;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.QueryHint;
import java.util.List;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {

    @QueryHints(@QueryHint(name = org.hibernate.annotations.QueryHints.CACHEABLE, value = "true"))
    User findByDiscordId(String discordId);

    @QueryHints(@QueryHint(name = org.hibernate.annotations.QueryHints.CACHEABLE, value = "true"))
    User findByTwitchId(String twitchId);

    @QueryHints(@QueryHint(name = org.hibernate.annotations.QueryHints.CACHEABLE, value = "true"))
    User findByTwitterId(String twitterId);

    User findByUsername(String username);

    boolean existsByUsernameIgnoreCase(String username);

    boolean existsByUsernameJapanese(String username);

    boolean existsByDiscordId(String discordId);

    boolean existsByTwitchId(String twitchId);

    @QueryHints(@QueryHint(name = org.hibernate.annotations.QueryHints.CACHEABLE, value = "true"))
    List<User> findByUsernameContainingIgnoreCaseAndEnabledTrue(String username);

}
