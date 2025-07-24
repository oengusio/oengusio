package app.oengus.adapter.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collection;

@Getter
@Setter
@Schema(description = "Representation of an array")
public class DataListDto<T> {
    @Schema(description = "The contents of this array")
    private Collection<T> data;

    public DataListDto() {
        this(new ArrayList<>());
    }

    public DataListDto(Collection<T> data) {
        this.data = data;
    }
}
