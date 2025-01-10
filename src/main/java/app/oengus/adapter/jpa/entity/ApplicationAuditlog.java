package app.oengus.adapter.jpa.entity;

import app.oengus.domain.volunteering.ApplicationStatus;
import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "application_auditlog")
public class ApplicationAuditlog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @JoinColumn(name = "application_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private ApplicationEntry application;

    @NotNull
    @Column(name = "timestamp")
    private LocalDateTime timestamp;

    @JoinColumn(name = "user_id")
    @ManyToOne(fetch = FetchType.EAGER)
    private User user;

    // TODO: old status

    @NotNull
    @Column(name = "new_status")
    @Enumerated(EnumType.STRING)
    private ApplicationStatus status;
}
