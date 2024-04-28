package app.oengus.application.port.persistence;

import app.oengus.domain.PledgeInfo;

import java.util.Optional;

public interface PatreonStatusPersistencePort {
    Optional<PledgeInfo> findByPatreonId(final String patreonId);

    void save(PledgeInfo pledge);
}
