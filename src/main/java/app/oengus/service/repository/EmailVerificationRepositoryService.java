package app.oengus.service.repository;

import app.oengus.dao.EmailVerificationRepository;
import app.oengus.entity.model.EmailVerification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailVerificationRepositoryService {
    private final EmailVerificationRepository emailVerificationRepository;

    public void save(EmailVerification emailVerification) {
        this.emailVerificationRepository.save(emailVerification);
    }
}
