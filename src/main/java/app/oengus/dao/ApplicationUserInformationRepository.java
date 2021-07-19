package app.oengus.dao;

import app.oengus.entity.model.Application;
import app.oengus.entity.model.ApplicationUserInformation;
import app.oengus.entity.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApplicationUserInformationRepository extends CrudRepository<ApplicationUserInformation, Integer> {
    ApplicationUserInformation getForUser(User user);

    ApplicationUserInformation getForApplication(Application application);
}
