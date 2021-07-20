package app.oengus.entity.model;

import app.oengus.entity.constants.ApplicationStatus;
import app.oengus.entity.model.api.ApplicationAuditlog;
import app.oengus.spring.model.Views;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonView;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "applications")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Application {

    @Id
    @JsonView(Views.Public.class)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @JoinColumn(name = "user_id")
    @JsonView(Views.Public.class)
    @OneToOne(fetch = FetchType.LAZY)
    private User user;

    @JsonView(Views.Internal.class)
    @JoinColumn(name = "marathon_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Marathon marathon;

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
    @Column(name = "references")
    @JsonView(Views.Public.class)
    private String references;

    @NotNull
    @Column(name = "application")
    @JsonView(Views.Public.class)
    private String application;

    @JsonManagedReference
    @OrderBy("timestamp DESC")
    @JsonView(Views.Public.class)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @OneToMany(mappedBy = "application", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ApplicationAuditlog> auditLogs;

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

    public Marathon getMarathon() {
        return marathon;
    }

    public void setMarathon(Marathon marathon) {
        this.marathon = marathon;
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
}
