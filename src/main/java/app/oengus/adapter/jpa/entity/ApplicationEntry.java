package app.oengus.adapter.jpa.entity;

import app.oengus.domain.volunteering.ApplicationStatus;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @JoinColumn(name = "user_id")
    @OneToOne(fetch = FetchType.LAZY)
    private User user;

    @JoinColumn(name = "team_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private TeamEntity team;

    @NotNull
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private ApplicationStatus status;

    @NotNull
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @NotNull
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @NotNull
    @Column(name = "reference")
    private String references;

    @NotNull
    @Column(name = "application")
    private String application;

    @OrderBy("timestamp DESC")
    @OneToMany(mappedBy = "application", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ApplicationAuditlog> auditLogs;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "application_availability", joinColumns = @JoinColumn(name = "application_id"))
    @AttributeOverrides({
        @AttributeOverride(name = "from", column = @Column(name = "date_from")),
        @AttributeOverride(name = "to", column = @Column(name = "date_to"))
    })
    @OrderBy("date_from ASC")
    // we can reuse this model as it has no submission related information
    private List<AvailabilityEntity> availabilities;
}
