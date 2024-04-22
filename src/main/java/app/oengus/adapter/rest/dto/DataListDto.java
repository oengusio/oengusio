package app.oengus.adapter.rest.dto;

import java.util.ArrayList;
import java.util.List;

public class DataListDto<T> {
    private List<T> data;

    public DataListDto() {
        this(new ArrayList<>());
    }

    public DataListDto(List<T> data) {
        this.data = data;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }
}
