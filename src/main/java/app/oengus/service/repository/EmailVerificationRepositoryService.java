package app.oengus.service.repository;

import app.oengus.dao.EmailVerificationRepository;
import app.oengus.entity.model.EmailVerification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmailVerificationRepositoryService {
    private final EmailVerificationRepository emailVerificationRepository;

    public Optional<EmailVerification> findByHash(String hash) {
        return this.emailVerificationRepository.findByVerificationHash(hash);
    }

    public void delete(EmailVerification emailVerification) {
        this.emailVerificationRepository.delete(emailVerification);
    }

    public void save(EmailVerification emailVerification) {
        this.emailVerificationRepository.save(emailVerification);
    }
}
