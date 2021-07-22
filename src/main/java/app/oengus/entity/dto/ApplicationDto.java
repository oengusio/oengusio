package app.oengus.entity.dto;

import app.oengus.entity.constants.ApplicationStatus;

import javax.annotation.Nullable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

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
}
