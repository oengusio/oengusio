package app.oengus.domain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Submissions for this marathon are closed.")
public class SubmissionsClosedException extends RuntimeException {
}
