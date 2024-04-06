package app.oengus.adapter.jpa.repository;

import app.oengus.adapter.jpa.entity.EmailVerification;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface EmailVerificationRepository extends CrudRepository<EmailVerification, Integer> {
    Optional<EmailVerification> findByVerificationHash(String hash);
}
