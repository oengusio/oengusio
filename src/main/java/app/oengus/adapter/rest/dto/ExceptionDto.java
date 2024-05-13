package app.oengus.adapter.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Schema(description = "Representation of a generic exception")
public class ExceptionDto {

    @Schema(description = "Message detailing what went wrong.")
    public String message;
}
