package app.oengus.entity.dto;

import app.oengus.adapter.jpa.entity.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.ZonedDateTime;
import java.util.List;

public class TeamDto {

    @NotNull
    @NotBlank
    @Size(max = 100)
    private String name;

    private String description;

    @Size(min = 1)
    private int teamSize;

    private boolean applicationsOpen;
    private ZonedDateTime applicationOpenDate;
    private ZonedDateTime applicationCloseDate;

    @NotNull
    private List<User> leaders;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getTeamSize() {
        return teamSize;
    }

    public void setTeamSize(int teamSize) {
        this.teamSize = teamSize;
    }

    public boolean isApplicationsOpen() {
        return applicationsOpen;
    }

    public void setApplicationsOpen(boolean applicationsOpen) {
        this.applicationsOpen = applicationsOpen;
    }

    public ZonedDateTime getApplicationOpenDate() {
        return applicationOpenDate;
    }

    public void setApplicationOpenDate(ZonedDateTime applicationOpenDate) {
        this.applicationOpenDate = applicationOpenDate;
    }

    public ZonedDateTime getApplicationCloseDate() {
        return applicationCloseDate;
    }

    public void setApplicationCloseDate(ZonedDateTime applicationCloseDate) {
        this.applicationCloseDate = applicationCloseDate;
    }

    public List<User> getLeaders() {
        return leaders;
    }

    public void setLeaders(List<User> leaders) {
        this.leaders = leaders;
    }
}
