package app.oengus.entity.dto;

import app.oengus.domain.volunteering.ApplicationStatus;
import app.oengus.entity.model.Availability;

import javax.annotation.Nullable;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
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
    private List<Availability> availabilities;

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

    public List<Availability> getAvailabilities() {
        return availabilities;
    }

    public void setAvailabilities(List<Availability> availabilities) {
        this.availabilities = availabilities;
    }
}
