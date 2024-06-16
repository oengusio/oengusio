package app.oengus.adapter.rest.dto.v2;

import app.oengus.adapter.rest.dto.v1.MarathonBasicInfoDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Schema
@Getter
@RequiredArgsConstructor
public class MarathonHomeDto {
    private final List<MarathonBasicInfoDto> live;
    private final List<MarathonBasicInfoDto> next;
    private final List<MarathonBasicInfoDto> open;
}
