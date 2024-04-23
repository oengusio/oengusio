package app.oengus.adapter.jpa.repository;

import app.oengus.adapter.jpa.entity.ApplicationUserInformation;
import app.oengus.adapter.jpa.entity.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ApplicationUserInformationRepository extends CrudRepository<ApplicationUserInformation, Integer> {
    Optional<ApplicationUserInformation> findByUser(User user);
}
