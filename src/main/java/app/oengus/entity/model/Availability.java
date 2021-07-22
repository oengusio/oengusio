package app.oengus.entity.model;

import app.oengus.spring.model.Views;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;
import org.hibernate.annotations.Cache;

@Embeddable
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
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

	public ZonedDateTime getFrom() {
		return this.from;
	}

	public void setFrom(final ZonedDateTime from) {
		this.from = from;
	}

	public ZonedDateTime getTo() {
		return this.to;
	}

	public void setTo(final ZonedDateTime to) {
		this.to = to;
	}
}
