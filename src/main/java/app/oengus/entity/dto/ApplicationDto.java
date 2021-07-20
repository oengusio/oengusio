package app.oengus.entity.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class ApplicationDto {
    @NotNull
    @Size(max = 255)
    private String references;

    @NotNull
    @NotBlank
    @Size(max = 255)
    private String application;

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
