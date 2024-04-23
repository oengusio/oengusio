package app.oengus.application.port.persistence;

import app.oengus.domain.PendingPasswordReset;

import java.util.Optional;

public interface PasswordResetPersistencePort {
    Optional<PendingPasswordReset> findByToken(String token);

    void save(PendingPasswordReset passwordReset);

    void delete(PendingPasswordReset passwordReset);
}
