package app.oengus.application.port.persistence;

import app.oengus.domain.Marathon;

import java.util.Optional;

public interface MarathonPersistencePort {
    Optional<Marathon> findById(String marathonId);
}
