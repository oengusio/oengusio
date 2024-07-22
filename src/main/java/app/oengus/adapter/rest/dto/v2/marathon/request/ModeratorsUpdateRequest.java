package app.oengus.adapter.rest.dto.v2.marathon.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ModeratorsUpdateRequest {
    private int[] userIds;
}
