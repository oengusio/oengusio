package app.oengus.dao;

import app.oengus.entity.model.Application;
import app.oengus.entity.model.Marathon;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApplicationRepository extends CrudRepository<Application, Integer> {
    List<Application> findByMarathon(Marathon marathon);
}
