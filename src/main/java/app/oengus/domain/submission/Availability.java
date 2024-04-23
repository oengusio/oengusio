package app.oengus.domain.submission;

import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

@Getter
@Setter
public class Availability {
    private ZonedDateTime from;
    private ZonedDateTime to;
}
