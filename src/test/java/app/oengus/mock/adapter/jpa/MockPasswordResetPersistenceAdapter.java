package app.oengus.mock.adapter.jpa;

import app.oengus.application.port.persistence.PasswordResetPersistencePort;
import app.oengus.domain.PendingPasswordReset;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Profile("test")
@RequiredArgsConstructor
public class MockPasswordResetPersistenceAdapter implements PasswordResetPersistencePort {
    @Override
    public Optional<PendingPasswordReset> findByToken(String token) {
        return Optional.empty();
    }

    @Override
    public void save(PendingPasswordReset passwordReset) {

    }

    @Override
    public void delete(PendingPasswordReset passwordReset) {

    }
}
