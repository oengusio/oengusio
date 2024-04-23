package app.oengus.adapter.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public class BooleanStatusDto {
    @Schema(description = "Boolean status for this request")
    private final boolean status;

    public BooleanStatusDto(boolean status) {
        this.status = status;
    }

    public boolean isStatus() {
        return status;
    }
}
