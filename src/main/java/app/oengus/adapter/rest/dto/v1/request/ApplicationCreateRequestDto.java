package app.oengus.adapter.rest.dto.v1.request;

import app.oengus.domain.submission.Availability;
import app.oengus.domain.volunteering.ApplicationStatus;

import javax.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public record ApplicationCreateRequestDto(
    @Nullable
    ApplicationStatus status,

    @NotNull
    @Size(max = 255)
    String references,

    @NotNull
    @NotBlank
    @Size(max = 255)
    String application,

    @Valid
    @NotNull
    @NotEmpty
    List<Availability> availabilities
) {
}
