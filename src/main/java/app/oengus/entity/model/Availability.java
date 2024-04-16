package app.oengus.entity.model;

import app.oengus.spring.model.Views;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;

@Getter
@Setter
@Embeddable
@JsonIgnoreProperties(ignoreUnknown = true)
public class Availability {

    @NotNull
    @Column(name = "date_from")
    @JsonView(Views.Public.class)
    private ZonedDateTime from;

    @NotNull
    @Column(name = "date_to")
    @JsonView(Views.Public.class)
    private ZonedDateTime to;
}
