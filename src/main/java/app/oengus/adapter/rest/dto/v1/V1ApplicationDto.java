package app.oengus.adapter.rest.dto.v1;

import app.oengus.domain.volunteering.ApplicationStatus;
import app.oengus.entity.model.Availability;

import java.time.LocalDateTime;
import java.util.List;

public record V1ApplicationDto(
    int id,
    V1UserDto user,
    Object team, // TODO
    ApplicationStatus status,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    String references,
    String application,
    List<Availability> availabilities
) {
}
