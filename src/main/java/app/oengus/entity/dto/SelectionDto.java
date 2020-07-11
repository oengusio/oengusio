package app.oengus.entity.dto;

import app.oengus.entity.model.Status;
import app.oengus.spring.model.Views;
import com.fasterxml.jackson.annotation.JsonView;

public class SelectionDto {

	@JsonView(Views.Public.class)
	private Integer id;

	@JsonView(Views.Public.class)
	private Integer categoryId;

	@JsonView(Views.Public.class)
	private Status status;

	public Integer getId() {
		return this.id;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	public Status getStatus() {
		return this.status;
	}

	public void setStatus(final Status status) {
		this.status = status;
	}

	public Integer getCategoryId() {
		return this.categoryId;
	}

	public void setCategoryId(final Integer categoryId) {
		this.categoryId = categoryId;
	}
}
