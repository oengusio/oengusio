package app.oengus.adapter.jpa.repository;

import app.oengus.adapter.jpa.entity.PasswordReset;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface PasswordResetsRepository extends CrudRepository<PasswordReset, Integer> {
    Optional<PasswordReset> findByToken(String token);
}
