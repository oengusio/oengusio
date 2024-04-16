package app.oengus.adapter.rest.dto.v1;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class V1OpponentDto {
    private int id;
    private int categoryId;
    private int submissionId; // Do i need the submission object here?
    private String video;
}
