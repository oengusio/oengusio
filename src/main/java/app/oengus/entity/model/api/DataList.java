package app.oengus.entity.model.api;

import java.util.List;

public class DataList<T> {

	private List<T> data;

	public List<T> getData() {
		return this.data;
	}

	public void setData(final List<T> data) {
		this.data = data;
	}
}
