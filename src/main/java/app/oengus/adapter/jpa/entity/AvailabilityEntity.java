package app.oengus.adapter.jpa.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;

@Getter
@Setter
@Embeddable
public class AvailabilityEntity {
    @NotNull
    @Column(name = "date_from")
    private ZonedDateTime from;

    @NotNull
    @Column(name = "date_to")
    private ZonedDateTime to;
}
