package app.oengus.entity.dto.v2;

import app.oengus.entity.dto.v2.marathon.CategoryDto;
import app.oengus.entity.model.Selection;
import app.oengus.entity.model.Status;

public class SelectionDto {
    private int id;
    private String marathonId;
    private CategoryDto category;
    private Status status;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMarathonId() {
        return marathonId;
    }

    public void setMarathonId(String marathonId) {
        this.marathonId = marathonId;
    }

    public CategoryDto getCategory() {
        return category;
    }

    public void setCategory(CategoryDto category) {
        this.category = category;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public static SelectionDto fromSelection(Selection model) {
        final SelectionDto dto = new SelectionDto();

        dto.setId(model.getId());
        dto.setMarathonId(model.getMarathon().getId());
        dto.setCategory(CategoryDto.fromCategory(model.getCategory()));
        dto.setStatus(model.getStatus());

        return dto;
    }
}
