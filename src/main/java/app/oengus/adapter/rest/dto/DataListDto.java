package app.oengus.adapter.rest.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collection;

@Getter
@Setter
public class DataListDto<T> {
    private Collection<T> data;

    public DataListDto() {
        this(new ArrayList<>());
    }

    public DataListDto(Collection<T> data) {
        this.data = data;
    }
}
