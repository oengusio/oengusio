package app.oengus.entity.dto;

import app.oengus.adapter.jpa.entity.MarathonEntity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class MarathonDto extends MarathonEntity {
	private BigDecimal donationsTotal;
	private boolean hasSubmitted;
}
