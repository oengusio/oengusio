package app.oengus.adapter.rest.dto.v1;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class V1GameDto {
    private int id;
    private int submissionId;
    private String name;
    private String description;
    private String console;
    private String ratio;
    private boolean emulated;
    private List<V1CategoryDto> categories;
}
