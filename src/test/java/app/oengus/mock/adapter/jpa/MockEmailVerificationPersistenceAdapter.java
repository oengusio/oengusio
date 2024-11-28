package app.oengus.mock.adapter.jpa;

import app.oengus.application.port.persistence.EmailVerificationPersistencePort;
import app.oengus.domain.PendingEmailVerification;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Profile("test")
@RequiredArgsConstructor
public class MockEmailVerificationPersistenceAdapter implements EmailVerificationPersistencePort {
    @Override
    public Optional<PendingEmailVerification> findByHash(String hash) {
        return Optional.empty();
    }

    @Override
    public void save(PendingEmailVerification pendingEmailVerification) {

    }

    @Override
    public void delete(PendingEmailVerification pendingEmailVerification) {

    }
}
