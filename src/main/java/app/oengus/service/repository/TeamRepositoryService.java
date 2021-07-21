package app.oengus.service.repository;

import app.oengus.dao.TeamRepository;
import app.oengus.entity.dto.TeamDto;
import app.oengus.entity.model.Marathon;
import app.oengus.entity.model.Team;
import app.oengus.helper.BeanHelper;
import javassist.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeamRepositoryService {
    private final TeamRepository teamRepository;

    public TeamRepositoryService(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }

    public Team getById(int id) throws NotFoundException {
        return this.teamRepository.findById(id).orElseThrow(() -> new NotFoundException("Team not found"));
    }

    public List<Team> getAll(String marathonId) {
        final Marathon marathon = new Marathon();
        marathon.setId(marathonId);

        return this.teamRepository.findByMarathon(marathon);
    }

    public Team create(String marathonId, TeamDto teamDto) {
        final Marathon marathon = new Marathon();
        marathon.setId(marathonId);
        final Team team = new Team();
        team.setId(-1);
        team.setMarathon(marathon);

        BeanHelper.copyProperties(teamDto, team);

        return this.teamRepository.save(team);
    }

    public Team update(int teamId, TeamDto patch) throws NotFoundException {
        final Team currentTeam = this.getById(teamId);

        BeanHelper.copyProperties(patch, currentTeam);

        return this.teamRepository.save(currentTeam);
    }
}
