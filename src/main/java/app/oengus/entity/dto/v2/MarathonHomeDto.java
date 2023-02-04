package app.oengus.entity.dto.v2;

import app.oengus.entity.dto.MarathonBasicInfoDto;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema
public class MarathonHomeDto {
    private List<MarathonBasicInfoDto> live;
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

    public List<MarathonBasicInfoDto> getLive() {
        return live;
    }

    public void setLive(List<MarathonBasicInfoDto> live) {
        this.live = live;
    }

    public List<MarathonBasicInfoDto> getNext() {
        return next;
    }

    public void setNext(List<MarathonBasicInfoDto> next) {
        this.next = next;
    }

    public List<MarathonBasicInfoDto> getOpen() {
        return open;
    }

    public void setOpen(List<MarathonBasicInfoDto> open) {
        this.open = open;
    }
}
