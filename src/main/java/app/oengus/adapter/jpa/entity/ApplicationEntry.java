package app.oengus.adapter.jpa.entity;

import app.oengus.domain.volunteering.ApplicationStatus;
import app.oengus.adapter.rest.Views;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "applications")
public class ApplicationEntry {

    @Id
    @JsonView(Views.Public.class)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @JoinColumn(name = "user_id")
    @JsonView(Views.Public.class)
    @OneToOne(fetch = FetchType.LAZY)
    private User user;

    @JsonBackReference
    @JsonView(Views.Public.class)
    @JoinColumn(name = "team_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private TeamEntity team;

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
    private List<AvailabilityEntity> availabilities;
}
