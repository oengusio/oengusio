package app.oengus.exception;

import io.sentry.Sentry;
import javassist.NotFoundException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.util.NestedServletException;

import javax.security.auth.login.LoginException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// TODO: Add proper json errors once we have the new front end going
//  https://spring.io/blog/2013/11/01/exception-handling-in-spring-mvc
@ControllerAdvice
public class OengusExceptionHandler {
    private static final Logger LOG = LoggerFactory.getLogger(OengusExceptionHandler.class);

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> accessDeniedException(final AccessDeniedException exc, final HttpServletRequest req) {
        final String header = req.getHeader("oengus-version");

        if (!"2".equals(header)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(toMap(req, exc));
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<?> notFoundException(final NotFoundException exc, final HttpServletRequest req) {
        final String header = req.getHeader("oengus-version");

        if (!"2".equals(header)) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(toMap(req, exc));
    }

    @ExceptionHandler(LoginException.class)
    public ResponseEntity<?> loginExceptionHandler(final LoginException exc, final HttpServletRequest req) {
        final String header = req.getHeader("oengus-version");

        if (!"2".equals(header)) {
            return ((ResponseEntity.BodyBuilder) ResponseEntity.notFound()).body(exc.getMessage());
        }

        return ResponseEntity.badRequest().body(toMap(req, exc));
    }

    @ExceptionHandler(NestedServletException.class)
    public ResponseEntity<?> validationException(final NestedServletException exc, final HttpServletRequest req) {
        final Throwable cause = ExceptionHelper.getRootCause(exc);

        if (cause instanceof ConstraintViolationException ex) {
            final Map<String, Object> stringStringMap = toMap(req, ex);

            stringStringMap.put("errors", ex.getConstraintViolations());

            return ResponseEntity.badRequest().body(stringStringMap);
        } else if (cause instanceof MissingServletRequestParameterException smh) {
            return ResponseEntity.badRequest().body(toMap(req, smh));
        }

        Sentry.captureException(exc);
        LOG.error("Uncaught NestedServletException", exc);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(toMap(req, exc));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> constraintViolationException(final ConstraintViolationException exc, final HttpServletRequest req) {
        final List<Map<String, String>> test = exc.getConstraintViolations()
            .stream()
            .map((v) -> {
                final Map<String, String> map = new HashMap<>();

                map.put("message", v.getMessage());
                map.put("propertyPath", v.getPropertyPath().toString());
                map.put("rootBeanClass", v.getRootBeanClass().toString());

                return map;
            })
            .toList();

        final Map<String, Object> stringStringMap = toMap(req, exc);

        stringStringMap.put("errors", test);

        return ResponseEntity.badRequest().body(stringStringMap);
    }

    @ExceptionHandler(OengusBusinessException.class)
    @ResponseStatus(value=HttpStatus.BAD_REQUEST)
    public ResponseEntity<?> oengusBusinessExceptionHandler(final OengusBusinessException e, final HttpServletRequest req) {
        final String header = req.getHeader("oengus-version");

        if (!"2".equals(header)) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

        return ResponseEntity.badRequest().body(toMap(req, e));
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

    // SOURCE: https://mtyurt.net/post/spring-how-to-handle-ioexception-broken-pipe.html
    @ExceptionHandler(IOException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public Object exceptionHandler(final HttpServletRequest req, final IOException e) {
        if (StringUtils.containsIgnoreCase(ExceptionUtils.getRootCauseMessage(e), "Broken pipe")) {
            return null;
        } else {
            Sentry.captureException(e);
            return toMap(req, e);
        }
    }

    private Map<String, Object> toMap(final HttpServletRequest req, final Exception exception) {
        final Map<String, Object> mapper = new HashMap<>();

        mapper.put("type", exception.getClass().getSimpleName());
        mapper.put("message", exception.getMessage());
        mapper.put("method", req.getMethod());
        mapper.put("path", req.getServletPath());

        return mapper;
    }
}
