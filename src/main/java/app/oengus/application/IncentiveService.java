package app.oengus.application;

import app.oengus.adapter.jpa.entity.Incentive;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class IncentiveService {
    public Incentive findById(int incentiveId) throws NotFoundException {
        throw new NotFoundException("Incentive not found");
    }

    public List<Incentive> findByMarathon(final String marathonId, final boolean withLocked,
                                          final boolean withUnapproved) throws NotFoundException {
        throw new NotFoundException("Marathon not found");
    }

    public List<Incentive> saveAll(final List<Incentive> incentives, final String marathonId) {
        return List.of();
    }

    public void deleteByMarathon(final String marathonId) {
        //
    }
}
