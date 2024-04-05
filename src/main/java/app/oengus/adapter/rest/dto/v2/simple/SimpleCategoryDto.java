package app.oengus.adapter.rest.dto.v2.simple;

import app.oengus.entity.model.Status;
import app.oengus.spring.model.Views;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Duration;

@Schema
public class SimpleCategoryDto {

    @Schema(hidden = true)
    @JsonView(Views.Internal.class)
    private int id;

    @JsonView(Views.Public.class)
    @Schema(description = "The name of this category")
    private String name;

    @JsonView(Views.Public.class)
    @Schema(description = "The estimated duration of this category")
    private Duration estimate;

    @JsonView(Views.Public.class)
    @Schema(description = "The acceptance status for this category")
    private Status status;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Duration getEstimate() {
        return estimate;
    }

    public void setEstimate(Duration estimate) {
        this.estimate = estimate;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
