package app.oengus.web.exception;

import app.oengus.application.exception.InvalidMFACodeException;
import app.oengus.application.exception.InvalidPasswordException;
import app.oengus.entity.dto.v2.auth.LoginResponseDto;
import app.oengus.web.v2.AuthApiController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice(assignableTypes = {AuthApiController.class})
public class AuthControllerExceptionHandler {
    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<LoginResponseDto> handleInvalidPassword() {
        return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(
                new LoginResponseDto()
                    .setStatus(LoginResponseDto.Status.USERNAME_PASSWORD_INCORRECT)
            );
    }

    @ExceptionHandler(InvalidMFACodeException.class)
    public ResponseEntity<LoginResponseDto> handleInvalidMFACode() {
        return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(
                new LoginResponseDto()
                    .setStatus(LoginResponseDto.Status.MFA_INVALID)
            );
    }
}
