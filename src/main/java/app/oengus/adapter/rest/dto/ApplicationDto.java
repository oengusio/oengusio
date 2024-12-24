package app.oengus.adapter.rest.dto;

import app.oengus.domain.volunteering.ApplicationStatus;
import app.oengus.adapter.jpa.entity.AvailabilityEntity;

import javax.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public class ApplicationDto {
    @Nullable
    private ApplicationStatus status;

    @NotNull
    @Size(max = 255)
    private String references;

    @NotNull
    @NotBlank
    @Size(max = 255)
    private String application;

    @Valid
    @NotNull
    @NotEmpty
    private List<AvailabilityEntity> availabilities;

    @Nullable
    public ApplicationStatus getStatus() {
        return status;
    }

    public void setStatus(@Nullable ApplicationStatus status) {
        this.status = status;
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

    public List<AvailabilityEntity> getAvailabilities() {
        return availabilities;
    }

    public void setAvailabilities(List<AvailabilityEntity> availabilities) {
        this.availabilities = availabilities;
    }
}
