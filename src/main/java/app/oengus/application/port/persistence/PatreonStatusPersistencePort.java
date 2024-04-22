package app.oengus.application.port.persistence;

import app.oengus.domain.PledgeInfo;

public interface PatreonStatusPersistencePort {
    void save(PledgeInfo pledge);
}
