package app.oengus.adapter.jpa;

import app.oengus.adapter.jpa.mapper.PasswordResetMapper;
import app.oengus.adapter.jpa.repository.PasswordResetsRepository;
import app.oengus.application.port.persistence.PasswordResetPersistencePort;
import app.oengus.domain.PendingPasswordReset;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PasswordResetPersistenceAdapter implements PasswordResetPersistencePort {
    private final PasswordResetsRepository repository;
    private final PasswordResetMapper mapper;

    @Override
    public Optional<PendingPasswordReset> findByToken(String token) {
        return this.repository.findByToken(token)
            .map(this.mapper::toDomain);
    }

    @Override
    public void save(PendingPasswordReset passwordReset) {
        final var resetModel = this.mapper.fromDomain(passwordReset);

        this.repository.save(resetModel);
    }

    @Override
    public void delete(PendingPasswordReset passwordReset) {
        final var resetModel = this.mapper.fromDomain(passwordReset);

        this.repository.delete(resetModel);
    }
}
