package app.oengus.adapter.rest.dto.v2;

import app.oengus.adapter.rest.dto.v1.MarathonBasicInfoDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Schema
@Getter
@Setter
public class MarathonHomeDto {
    private final List<MarathonBasicInfoDto> live;
    private List<MarathonBasicInfoDto> next;
    private List<MarathonBasicInfoDto> open;

    public MarathonHomeDto(List<MarathonBasicInfoDto> live, List<MarathonBasicInfoDto> next, List<MarathonBasicInfoDto> open) {
        this.live = live;
        this.next = next;
        this.open = open;
    }

    public MarathonHomeDto() {
        this(List.of(), List.of(), List.of());
    }
}
