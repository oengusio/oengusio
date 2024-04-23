package app.oengus.adapter.jpa.repository;

import app.oengus.adapter.jpa.entity.SocialAccount;
import org.springframework.data.repository.CrudRepository;

public interface SocialAccountRepository extends CrudRepository<SocialAccount, Integer> {
}
