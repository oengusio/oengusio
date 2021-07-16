package app.oengus.entity.model;

import app.oengus.entity.constants.ApplicationStatus;
import app.oengus.spring.model.Views;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonView;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "applications")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Application {

    @Id
    @JsonBackReference
    @JoinColumn(name = "user_id")
    @JsonView(Views.Public.class)
    @OneToOne(fetch = FetchType.LAZY)
    private User user;

    @Id
    @JsonBackReference
    @JsonView(Views.Public.class)
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
}
