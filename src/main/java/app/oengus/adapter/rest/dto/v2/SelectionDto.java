package app.oengus.adapter.rest.dto.v2;

import app.oengus.adapter.rest.dto.v2.marathon.CategoryDto;
import app.oengus.domain.submission.Status;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SelectionDto {
    private int id;
    private String marathonId;
    private CategoryDto category;
    private Status status;
}
