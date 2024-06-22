package app.oengus.adapter.rest.dto.v1;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OpponentSubmissionDto {
	private int id;
	private List<V1UserDto> users;
	private String gameName;
	private int categoryId;
	private String categoryName;
	private String video;
}
