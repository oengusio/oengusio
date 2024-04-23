package app.oengus.adapter.rest.dto.v2.simple;

import app.oengus.domain.submission.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.Duration;

@Getter
@Setter
@Schema
public class SimpleCategoryDto {

    @Schema(hidden = true)
    private int id;

    @Schema(description = "The name of this category")
    private String name;

    @Schema(description = "The estimated duration of this category")
    private Duration estimate;

    @Schema(description = "The acceptance status for this category, may be null")
    private Status status;
}
