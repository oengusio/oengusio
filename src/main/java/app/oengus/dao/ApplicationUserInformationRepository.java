package app.oengus.dao;

import app.oengus.entity.model.ApplicationUserInformation;
import app.oengus.entity.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ApplicationUserInformationRepository extends CrudRepository<ApplicationUserInformation, Integer> {
    Optional<ApplicationUserInformation> findByUser(User user);
}
