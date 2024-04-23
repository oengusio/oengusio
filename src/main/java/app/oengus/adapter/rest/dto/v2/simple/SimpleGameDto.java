package app.oengus.adapter.rest.dto.v2.simple;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Schema
public class SimpleGameDto {

    @Schema(hidden = true)
    private int id;

    @Schema(description = "The name of this game")
    private String name;

    @Schema(description = "The submitted categories for this game")
    private List<SimpleCategoryDto> categories;
}
