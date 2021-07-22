package app.oengus.entity.dto;

import app.oengus.entity.constants.ApplicationStatus;

public class UserApplicationHistoryDto {
    private String marathonName;
    private String marathonId;

    private String teamName;
    private int teamId;

    private ApplicationStatus status;

    public String getMarathonName() {
        return marathonName;
    }

    public void setMarathonName(String marathonName) {
        this.marathonName = marathonName;
    }

    public String getMarathonId() {
        return marathonId;
    }

    public void setMarathonId(String marathonId) {
        this.marathonId = marathonId;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public int getTeamId() {
        return teamId;
    }

    public void setTeamId(int teamId) {
        this.teamId = teamId;
    }

    public ApplicationStatus getStatus() {
        return status;
    }

    public void setStatus(ApplicationStatus status) {
        this.status = status;
    }
}
