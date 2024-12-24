package app.oengus.adapter.rest.advice;

import app.oengus.domain.exception.InvalidUsernameException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import jakarta.servlet.http.HttpServletRequest;

import static app.oengus.adapter.rest.advice.HandlerHelpers.toMap;

@ControllerAdvice
public class UserExceptionHandler {
    @ExceptionHandler(InvalidUsernameException.class)
    public ResponseEntity<?> handleInvalidUsernameException(final HttpServletRequest req, final InvalidUsernameException ex) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
            .header("Content-Type", "application/json")
            .body(toMap(req, ex));
    }
}
