package app.oengus.service.repository;

import app.oengus.dao.PatreonStatusRepository;
import app.oengus.entity.dto.PatreonStatusDto;
import app.oengus.entity.model.PatreonStatus;
import app.oengus.helper.BeanHelper;
import org.springframework.stereotype.Service;

@Service
@Deprecated(forRemoval = true)
public class PatreonStatusRepositoryService {
    private final PatreonStatusRepository repository;

    public PatreonStatusRepositoryService(PatreonStatusRepository repository) {
        this.repository = repository;
    }

    public PatreonStatus update(PatreonStatusDto patch) {
        final PatreonStatus patreonStatus = new PatreonStatus();

        BeanHelper.copyProperties(patch, patreonStatus);

        return this.repository.save(patreonStatus);
    }

    public PatreonStatus update(PatreonStatus patch) {
        return this.repository.save(patch);
    }
}
