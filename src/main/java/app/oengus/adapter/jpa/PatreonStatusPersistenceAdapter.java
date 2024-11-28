package app.oengus.adapter.jpa;

import app.oengus.adapter.jpa.mapper.PatreonStatusMapper;
import app.oengus.adapter.jpa.repository.PatreonStatusRepository;
import app.oengus.application.port.persistence.PatreonStatusPersistencePort;
import app.oengus.domain.PledgeInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Profile("!test")
@RequiredArgsConstructor
public class PatreonStatusPersistenceAdapter implements PatreonStatusPersistencePort {
    private final PatreonStatusRepository repository;
    private final PatreonStatusMapper mapper;

    @Override
    public Optional<PledgeInfo> findByPatreonId(String patreonId) {
        return this.repository.findById(patreonId)
            .map(this.mapper::toDomain);
    }

    @Override
    public void save(PledgeInfo pledge) {
        final var entity = this.mapper.fromDomain(pledge);

        this.repository.save(entity);
    }
}
