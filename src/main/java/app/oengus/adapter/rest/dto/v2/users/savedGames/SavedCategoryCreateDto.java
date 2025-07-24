package app.oengus.adapter.rest.dto.v2.users.savedGames;

import app.oengus.domain.submission.Category;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.time.DurationMin;

import java.time.Duration;

@Schema(description = "Category that is attached to a saved game.")
public record SavedCategoryCreateDto(
    @NotBlank
    @Size(max = Category.NAME_MAX_LENGTH)
    @Schema(description = "The name of this category. E.G. Any%")
    String name,

    @NotBlank
    @Size(max = Category.DESCRIPTION_MAX_LENGTH)
    @Schema(description = "A paragraph detailing what this category is about")
    String description,

    @NotNull
    @DurationMin(seconds = 1)
    @Schema(
        description = "Approximate duration of the run. Formatted in the ISO-8601 duration format.",
        example = "PT5H30M20S"
    )
    Duration estimate,

    @NotBlank
    @Size(max = Category.VIDEO_MAX_LENGTH)
    @Schema(description = "Showcase video of this category.")
    String video
) {
}
