package app.oengus.dao;

import app.oengus.entity.model.Application;
import app.oengus.entity.model.Marathon;
import app.oengus.entity.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends CrudRepository<Application, Integer> {
    List<Application> findByMarathon(Marathon marathon);

    Optional<Application> findByMarathonAndUser(Marathon marathon, User user);
}
