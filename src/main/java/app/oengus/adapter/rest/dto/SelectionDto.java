package app.oengus.adapter.rest.dto;

import app.oengus.domain.submission.Status;
import app.oengus.adapter.rest.Views;
import com.fasterxml.jackson.annotation.JsonView;

public class SelectionDto {

	@JsonView(Views.Public.class)
	private int id;

	@JsonView(Views.Public.class)
	private int categoryId;

	@JsonView(Views.Public.class)
	private Status status;

	public int getId() {
		return this.id;
	}

	public void setId(final int id) {
		this.id = id;
	}

	public Status getStatus() {
		return this.status;
	}

	public void setStatus(final Status status) {
		this.status = status;
	}

	public int getCategoryId() {
		return this.categoryId;
	}

	public void setCategoryId(final int categoryId) {
		this.categoryId = categoryId;
	}
}
