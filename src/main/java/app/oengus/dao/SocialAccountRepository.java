package app.oengus.dao;

import app.oengus.entity.model.SocialAccount;
import app.oengus.entity.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SocialAccountRepository extends JpaRepository<SocialAccount, Integer> {
}
