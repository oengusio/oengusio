package app.oengus.adapter.jpa.repository;

import app.oengus.entity.model.SocialAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

public interface SocialAccountRepository extends CrudRepository<SocialAccount, Integer> {
}
