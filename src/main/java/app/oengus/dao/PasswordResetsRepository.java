package app.oengus.dao;

import app.oengus.entity.model.PasswordReset;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface PasswordResetsRepository extends CrudRepository<PasswordReset, Integer> {
    Optional<PasswordReset> findByToken(String token);
}
