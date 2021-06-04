package app.oengus.exception;

import javassist.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

// TODO: Add proper json errors once we have the new front end going
//  https://spring.io/blog/2013/11/01/exception-handling-in-spring-mvc
@ControllerAdvice
public class OengusExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<?> notFoundException(final NotFoundException e, final HttpServletRequest req) {
        final String header = req.getHeader("oengus-version");

        if (!"2".equals(header)) {
            return ResponseEntity.notFound().build();
        }

        final Map<String, String> mapper = new HashMap<>();

        mapper.put("type", e.getClass().getSimpleName());
        mapper.put("message", e.getMessage());
        mapper.put("method", req.getMethod());
        mapper.put("path", req.getServletPath());

        return ResponseEntity.badRequest().body(mapper);
    }

    // TODO: find all parts that catch this exception and remove it
    @ExceptionHandler(OengusBusinessException.class)
    @ResponseStatus(value=HttpStatus.BAD_REQUEST)
    public ResponseEntity<?> oengusBusinessExceptionHandler(final OengusBusinessException e, final HttpServletRequest req) {
        final String header = req.getHeader("oengus-version");

        if (!"2".equals(header)) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

        final Map<String, String> mapper = new HashMap<>();

        mapper.put("type", e.getClass().getSimpleName());
        mapper.put("message", e.getMessage());
        mapper.put("method", req.getMethod());
        mapper.put("path", req.getServletPath());

        return ResponseEntity.badRequest().body(mapper);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(value=HttpStatus.NOT_FOUND)
    @ResponseBody
    public Map<String, String> requestHandlingNoHandlerFound(final NoHandlerFoundException e) {
        final Map<String, String> mapper = new HashMap<>();

        mapper.put("type", "NotFoundException");
        mapper.put("message", "The requested page was not found");
        mapper.put("method", e.getHttpMethod());
        mapper.put("path", e.getRequestURL());

        return mapper;
    }
}
