package app.oengus.adapter.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Schema
@RequiredArgsConstructor
public class BooleanStatusDto {
    @Schema(description = "Boolean status for this request")
    private final boolean status;
}
