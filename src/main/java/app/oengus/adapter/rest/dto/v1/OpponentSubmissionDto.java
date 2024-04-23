package app.oengus.adapter.rest.dto.v1;

import app.oengus.adapter.rest.Views;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OpponentSubmissionDto {
	@JsonView(Views.Public.class)
	private int id;

	@JsonView(Views.Public.class)
	private List<V1UserDto> users;

	@JsonView(Views.Public.class)
	private String gameName;

	@JsonView(Views.Public.class)
	private int categoryId;

	@JsonView(Views.Public.class)
	private String categoryName;

	@JsonView(Views.Public.class)
	private String video;
}
