package app.oengus.adapter.rest.advice;

import app.oengus.application.exception.InvalidMFACodeException;
import app.oengus.application.exception.InvalidPasswordException;
import app.oengus.adapter.rest.dto.v2.auth.LoginResponseDto;
import app.oengus.application.exception.auth.UnknownServiceException;
import app.oengus.application.exception.auth.UnknownUserException;
import app.oengus.application.exception.auth.UserDisabledException;
import app.oengus.adapter.rest.controller.v2.AuthApiController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice(assignableTypes = {AuthApiController.class})
public class AuthControllerAdvice {
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

    @ExceptionHandler(UnknownUserException.class)
    public ResponseEntity<LoginResponseDto> handleUnknownUser() {
        return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(
                new LoginResponseDto()
                    .setStatus(LoginResponseDto.Status.OAUTH_ACCOUNT_NOT_FOUND)
            );
    }

    @ExceptionHandler(UnknownServiceException.class)
    public ResponseEntity<LoginResponseDto> handleUnknownService() {
        return ResponseEntity
            .status(HttpStatus.UNPROCESSABLE_ENTITY)
            .body(
                new LoginResponseDto()
                    .setStatus(LoginResponseDto.Status.USERNAME_PASSWORD_INCORRECT)
            );
    }

    @ExceptionHandler(UserDisabledException.class)
    public ResponseEntity<LoginResponseDto> handleUserDisabled() {
        return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(
                new LoginResponseDto()
                    .setStatus(LoginResponseDto.Status.ACCOUNT_DISABLED)
            );
    }
}
