package app.oengus.entity.dto;

public class OrderDto {

	private String id;

	public OrderDto(final String id) {
		this.id = id;
	}

	public String getId() {
		return this.id;
	}

	public void setId(final String id) {
		this.id = id;
	}
}
