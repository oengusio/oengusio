package app.oengus.entity.dto;

import app.oengus.entity.model.User;
import app.oengus.spring.model.Views;
import com.fasterxml.jackson.annotation.JsonView;

import java.util.List;

public class OpponentSubmissionDto {


	@JsonView(Views.Public.class)
	private Integer id;

	@JsonView(Views.Public.class)
	private List<User> users;

	@JsonView(Views.Public.class)
	private String gameName;

	@JsonView(Views.Public.class)
	private Integer categoryId;

	@JsonView(Views.Public.class)
	private String categoryName;

	@JsonView(Views.Public.class)
	private String video;

	public Integer getId() {
		return this.id;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	public List<User> getUsers() {
		return this.users;
	}

	public void setUsers(final List<User> users) {
		this.users = users;
	}

	public String getGameName() {
		return this.gameName;
	}

	public void setGameName(final String gameName) {
		this.gameName = gameName;
	}

	public Integer getCategoryId() {
		return this.categoryId;
	}

	public void setCategoryId(final Integer categoryId) {
		this.categoryId = categoryId;
	}

	public String getCategoryName() {
		return this.categoryName;
	}

	public void setCategoryName(final String categoryName) {
		this.categoryName = categoryName;
	}

	public String getVideo() {
		return this.video;
	}

	public void setVideo(final String video) {
		this.video = video;
	}
}
