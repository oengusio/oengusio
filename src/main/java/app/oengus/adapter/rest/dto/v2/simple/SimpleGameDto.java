package app.oengus.adapter.rest.dto.v2.simple;

import app.oengus.spring.model.Views;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema
public class SimpleGameDto {

    @Schema(hidden = true)
    @JsonView(Views.Internal.class)
    private int id;

    @JsonView(Views.Public.class)
    @Schema(description = "The name of this game")
    private String name;

    @JsonView(Views.Public.class)
    @Schema(description = "The submitted categories for this game")
    private List<SimpleCategoryDto> categories;

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

    public List<SimpleCategoryDto> getCategories() {
        return categories;
    }

    public void setCategories(List<SimpleCategoryDto> categories) {
        this.categories = categories;
    }
}
