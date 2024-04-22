package app.oengus.adapter.rest.dto.v1;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class V1OpponentDto {
    private int id;
    private int categoryId;
    private int submissionId;
    private String video;

    private String gameName;
    private String categoryName;
    private List<V1UserDto> users = new ArrayList<>();
}
