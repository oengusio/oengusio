package app.oengus.entity.model.api;

import app.oengus.entity.constants.ApplicationStatus;
import app.oengus.entity.model.Application;
import app.oengus.entity.model.Marathon;
import app.oengus.entity.model.User;
import app.oengus.spring.model.Views;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonView;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "application_auditlog")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class ApplicationAuditlog {

    @Id
    @JsonView(Views.Public.class)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @JsonBackReference
    @JsonView(Views.Public.class)
    @JoinColumn(name = "application_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Application application;

    @NotNull
    @Column(name = "timestamp")
    @JsonView(Views.Public.class)
    private LocalDateTime timestamp;

    @JoinColumn(name = "user_id")
    @JsonView(Views.Public.class)
    @ManyToOne(fetch = FetchType.EAGER)
    private User user;

    @NotNull
    @Column(name = "new_status")
    @Enumerated(EnumType.STRING)
    @JsonView(Views.Public.class)
    private ApplicationStatus status;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ApplicationStatus getStatus() {
        return status;
    }

    public void setStatus(ApplicationStatus status) {
        this.status = status;
    }
}
