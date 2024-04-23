package app.oengus.adapter.rest.dto;

import app.oengus.adapter.rest.dto.v1.V1UserDto;
import app.oengus.domain.submission.Availability;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OpponentCategoryDto {
	private int id;
	private V1UserDto user;
	private String video;
	private List<Availability> availabilities;
}
