package app.oengus.application.port.persistence;

import app.oengus.domain.PendingEmailVerification;

import java.util.Optional;

// TODO: make a service for this.
public interface EmailVerificationPersistencePort {
    Optional<PendingEmailVerification> findByHash(String hash);

    void save(PendingEmailVerification pendingEmailVerification);

    void delete(PendingEmailVerification pendingEmailVerification);
}
