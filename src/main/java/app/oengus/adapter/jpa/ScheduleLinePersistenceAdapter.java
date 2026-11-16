package app.oengus.adapter.jpa;

import app.oengus.adapter.jpa.repository.ScheduleLineRepository;
import app.oengus.application.port.persistence.ScheduleLinePersistencePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ScheduleLinePersistenceAdapter implements ScheduleLinePersistencePort {
    private final ScheduleLineRepository slr;

    @Override
    @Transactional
    public void reassignRunner(int oldRunnerId, int newRunnerId) {
        this.slr.updateLinesSetUserIdWhereNewUserId(oldRunnerId, newRunnerId);
    }
}
