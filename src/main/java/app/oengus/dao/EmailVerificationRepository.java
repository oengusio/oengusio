package app.oengus.dao;

import app.oengus.entity.model.EmailVerification;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmailVerificationRepository extends CrudRepository<EmailVerification, Integer> {
    Optional<EmailVerification> findByVerificationHash(String hash);
}
