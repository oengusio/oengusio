package app.oengus.adapter.jpa;

import app.oengus.adapter.jpa.mapper.MarathonMapper;
import app.oengus.adapter.jpa.repository.MarathonRepository;
import app.oengus.application.port.persistence.MarathonPersistencePort;
import app.oengus.domain.Marathon;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MarathonPersistenceAdapter implements MarathonPersistencePort {
    private final MarathonRepository repository;
    private final MarathonMapper mapper;

    @Override
    public Optional<Marathon> findById(String marathonId) {
        return Optional.empty();
    }
}
