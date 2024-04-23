package app.oengus.adapter.rest.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class DataListDto<T> {
    private List<T> data;

    public DataListDto() {
        this(new ArrayList<>());
    }

    public DataListDto(List<T> data) {
        this.data = data;
    }
}
