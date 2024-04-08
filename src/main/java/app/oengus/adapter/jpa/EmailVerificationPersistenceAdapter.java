package app.oengus.adapter.jpa;

import app.oengus.adapter.jpa.mapper.EmailVerificationMapper;
import app.oengus.adapter.jpa.repository.EmailVerificationRepository;
import app.oengus.application.port.persistence.EmailVerificationPersistencePort;
import app.oengus.domain.PendingEmailVerification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class EmailVerificationPersistenceAdapter implements EmailVerificationPersistencePort {
    private final EmailVerificationRepository emailVerificationRepository;
    private final EmailVerificationMapper mapper;

    @Override
    public Optional<PendingEmailVerification> findByHash(String hash) {
        return this.emailVerificationRepository.findByVerificationHash(hash)
            .map(this.mapper::toDomain);
    }

    @Override
    public void save(PendingEmailVerification pendingEmailVerification) {
        final var emailVerification = this.mapper.fromDomain(pendingEmailVerification);

        this.emailVerificationRepository.save(emailVerification);
    }

    @Override
    public void delete(PendingEmailVerification pendingEmailVerification) {
        final var emailVerification = this.mapper.fromDomain(pendingEmailVerification);

        this.emailVerificationRepository.delete(emailVerification);
    }
}
