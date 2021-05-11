package app.oengus.exception;

import javassist.NotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

// TODO: Add proper json errors once we have the new front end going
//  https://spring.io/blog/2013/11/01/exception-handling-in-spring-mvc
@ControllerAdvice
public class OengusExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<?> notFoundException() {
        return ResponseEntity.notFound().build();
    }

    // TODO: find all parts that catch this exception and remove it
    @ExceptionHandler(OengusBusinessException.class)
    public ResponseEntity<?> oengusBusinessExceptionHandler(final OengusBusinessException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

}
