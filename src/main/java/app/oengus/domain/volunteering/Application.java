package app.oengus.domain.volunteering;

import app.oengus.adapter.jpa.entity.AvailabilityEntity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
public class Application {
    private final int id;
    private final int userId;
    private final int teamId;

    private ApplicationStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private String references;
    private String application;

    private List<AuditLog> auditLogs;

    private List<AvailabilityEntity> availabilities;

    @Getter
    @Setter
    @RequiredArgsConstructor
    public static class AuditLog {
        private final int id;
        private final int userId;
        private LocalDateTime timestamp;
        private ApplicationStatus status;
    }
}
