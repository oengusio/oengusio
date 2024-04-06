package app.oengus.entity.model;

import app.oengus.adapter.jpa.entity.User;
import app.oengus.spring.model.Views;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonView;

import javax.annotation.Nullable;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.ZonedDateTime;
import java.util.List;

@Entity
@Table(name = "teams")
public class Team {

    @Id
    @JsonView(Views.Public.class)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @JsonBackReference
    @JsonView(Views.Internal.class)
    @JoinColumn(name = "marathon_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Marathon marathon;

    @NotNull
    @NotBlank
    @Size(max = 100)
    @Column(name = "name")
    @JsonView(Views.Public.class)
    private String name;

    @Column(name = "description")
    @JsonView(Views.Public.class)
    private String description;

    @NotNull
    @Size(min = 1)
    @Column(name = "team_size")
    @JsonView(Views.Public.class)
    private int teamSize;

    @NotNull
    @Column(name = "applications_open")
    @JsonView(Views.Public.class)
    private boolean applicationsOpen;

    @Nullable
    @Column(name = "application_open_date")
    @JsonView(Views.Public.class)
    private ZonedDateTime applicationOpenDate;

    @Nullable
    @Column(name = "application_close_date")
    @JsonView(Views.Public.class)
    private ZonedDateTime applicationCloseDate;

    @ManyToMany
    @JoinTable(
        name = "team_leaders",
        joinColumns = {@JoinColumn(name = "team_id")},
        inverseJoinColumns = {@JoinColumn(name = "user_id")}
    )
    @OrderBy(value = "id ASC")
    @JsonView(Views.Public.class)
    private List<User> leaders;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Marathon getMarathon() {
        return marathon;
    }

    public void setMarathon(Marathon marathon) {
        this.marathon = marathon;
    }

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

    @Nullable
    public ZonedDateTime getApplicationOpenDate() {
        return applicationOpenDate;
    }

    public void setApplicationOpenDate(@Nullable ZonedDateTime applicationOpenDate) {
        this.applicationOpenDate = applicationOpenDate;
    }

    @Nullable
    public ZonedDateTime getApplicationCloseDate() {
        return applicationCloseDate;
    }

    public void setApplicationCloseDate(@Nullable ZonedDateTime applicationCloseDate) {
        this.applicationCloseDate = applicationCloseDate;
    }

    public List<User> getLeaders() {
        return leaders;
    }

    public void setLeaders(List<User> leaders) {
        this.leaders = leaders;
    }
}
