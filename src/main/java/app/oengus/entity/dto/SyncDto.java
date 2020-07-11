package app.oengus.entity.dto;

public class SyncDto {

	private String id;
	private String name;

	public SyncDto(final String id, final String name) {
		this.id = id;
		this.name = name;
	}

	public String getId() {
		return this.id;
	}

	public void setId(final String id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(final String name) {
		this.name = name;
	}
}
