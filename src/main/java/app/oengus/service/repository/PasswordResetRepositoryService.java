package app.oengus.service.repository;

import app.oengus.dao.PasswordResetsRepository;
import app.oengus.entity.model.PasswordReset;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PasswordResetRepositoryService {
    private final PasswordResetsRepository passwordResetRepository;

    public PasswordReset save(PasswordReset passwordReset) {
        return this.passwordResetRepository.save(passwordReset);
    }

    public Optional<PasswordReset> findByToken(String token) {
        return this.passwordResetRepository.findByToken(token);
    }
}
