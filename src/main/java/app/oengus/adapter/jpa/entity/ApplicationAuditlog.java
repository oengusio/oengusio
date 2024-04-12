package app.oengus.adapter.jpa.entity;

import app.oengus.domain.volunteering.ApplicationStatus;
import app.oengus.spring.model.Views;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "application_auditlog")
public class ApplicationAuditlog {

    @Id
    @JsonView(Views.Public.class)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @JsonBackReference
    @JsonView(Views.Public.class)
    @JoinColumn(name = "application_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private ApplicationEntry application;

    @NotNull
    @Column(name = "timestamp")
    @JsonView(Views.Public.class)
    private LocalDateTime timestamp;

    @JoinColumn(name = "user_id")
    @JsonView(Views.Public.class)
    @ManyToOne(fetch = FetchType.EAGER)
    private User user;

    // TODO: old status

    @NotNull
    @Column(name = "new_status")
    @Enumerated(EnumType.STRING)
    @JsonView(Views.Public.class)
    private ApplicationStatus status;
}
