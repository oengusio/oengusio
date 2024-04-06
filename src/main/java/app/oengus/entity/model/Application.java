package app.oengus.entity.model;

import app.oengus.adapter.jpa.entity.User;
import app.oengus.entity.constants.ApplicationStatus;
import app.oengus.entity.model.api.ApplicationAuditlog;
import app.oengus.spring.model.Views;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonView;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "applications")
public class Application {

    @Id
    @JsonView(Views.Public.class)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @JoinColumn(name = "user_id")
    @JsonView(Views.Public.class)
    @OneToOne(fetch = FetchType.LAZY)
    private User user;

    @JsonBackReference
    @JsonView(Views.Public.class)
    @JoinColumn(name = "team_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Team team;

    @NotNull
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    @JsonView(Views.Public.class)
    private ApplicationStatus status;

    @NotNull
    @Column(name = "created_at")
    @JsonView(Views.Public.class)
    private LocalDateTime createdAt;

    @NotNull
    @Column(name = "updated_at")
    @JsonView(Views.Public.class)
    private LocalDateTime updatedAt;

    @NotNull
    @Column(name = "reference")
    @JsonView(Views.Public.class)
    private String references;

    @NotNull
    @Column(name = "application")
    @JsonView(Views.Public.class)
    private String application;

    @JsonManagedReference
    @OrderBy("timestamp DESC")
    @JsonView(Views.Public.class)
    @OneToMany(mappedBy = "application", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ApplicationAuditlog> auditLogs;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "application_availability", joinColumns = @JoinColumn(name = "application_id"))
    @AttributeOverrides({
        @AttributeOverride(name = "from", column = @Column(name = "date_from")),
        @AttributeOverride(name = "to", column = @Column(name = "date_to"))
    })
    @OrderBy("date_from ASC")
    @JsonView(Views.Public.class)
    // we can reuse this model as it has no submission related information
    private List<Availability> availabilities;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public ApplicationStatus getStatus() {
        return status;
    }

    public void setStatus(ApplicationStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getReferences() {
        return references;
    }

    public void setReferences(String references) {
        this.references = references;
    }

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public List<ApplicationAuditlog> getAuditLogs() {
        return auditLogs;
    }

    public void setAuditLogs(List<ApplicationAuditlog> auditLogs) {
        if (this.auditLogs == null) {
            this.auditLogs = new ArrayList<>();
        }

        this.auditLogs.clear();
        this.auditLogs.addAll(auditLogs);
    }

    public List<Availability> getAvailabilities() {
        return availabilities;
    }

    public void setAvailabilities(List<Availability> availabilities) {
        if (this.availabilities == null) {
            this.availabilities = new ArrayList<>();
        }

        this.availabilities.clear();
        this.availabilities.addAll(availabilities);
    }
}
