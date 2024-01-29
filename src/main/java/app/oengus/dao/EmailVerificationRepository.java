package app.oengus.dao;

import app.oengus.entity.model.EmailVerification;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailVerificationRepository extends CrudRepository<EmailVerification, Integer> {
}
