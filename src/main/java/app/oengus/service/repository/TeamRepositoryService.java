package app.oengus.service.repository;

import app.oengus.dao.TeamRepository;
import app.oengus.entity.dto.TeamDto;
import app.oengus.adapter.jpa.entity.MarathonEntity;
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
        final MarathonEntity marathon = new MarathonEntity();
        marathon.setId(marathonId);

        return this.teamRepository.findByMarathon(marathon);
    }

    public Team create(String marathonId, TeamDto teamDto) {
        final MarathonEntity marathon = new MarathonEntity();
        marathon.setId(marathonId);
        final Team team = new Team();
        team.setId(-1);
        team.setMarathon(marathon);

        BeanHelper.copyProperties(teamDto, team);

        team.setApplicationOpenDate(team.getApplicationOpenDate().withSecond(0));
        team.setApplicationCloseDate(team.getApplicationCloseDate().withSecond(0));

        return this.teamRepository.save(team);
    }

    public Team update(int teamId, TeamDto patch) throws NotFoundException {
        final Team currentTeam = this.getById(teamId);

        BeanHelper.copyProperties(patch, currentTeam);

        currentTeam.setApplicationOpenDate(currentTeam.getApplicationOpenDate().withSecond(0));
        currentTeam.setApplicationCloseDate(currentTeam.getApplicationCloseDate().withSecond(0));

        return this.teamRepository.save(currentTeam);
    }
}
