package app.oengus.adapter.rest.advice;

import app.oengus.adapter.rest.controller.v2.ScheduleApiController;
import app.oengus.domain.exception.InvalidExportFormatException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice(assignableTypes = {
    ScheduleApiController.class
})
public class ScheduleControllerAdvice {

    @ExceptionHandler(InvalidExportFormatException.class)
    public ResponseEntity<JsonNode> handleInvalidFormatException(final InvalidExportFormatException ex) {
        final var jackson = new JsonMapper();

        return ResponseEntity
            .status(HttpStatus.UNPROCESSABLE_ENTITY)
            .contentType(MediaType.APPLICATION_JSON)
            .body(
                jackson.createObjectNode()
                    .put("message", ex.getMessage())
            );
    }
}
