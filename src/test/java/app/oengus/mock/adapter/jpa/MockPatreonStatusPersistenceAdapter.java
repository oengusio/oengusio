package app.oengus.mock.adapter.jpa;

import app.oengus.application.port.persistence.PatreonStatusPersistencePort;
import app.oengus.domain.PledgeInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Profile("test")
@RequiredArgsConstructor
public class MockPatreonStatusPersistenceAdapter implements PatreonStatusPersistencePort {
    @Override
    public Optional<PledgeInfo> findByPatreonId(String patreonId) {
        return Optional.empty();
    }

    @Override
    public void save(PledgeInfo pledge) {

    }
}
