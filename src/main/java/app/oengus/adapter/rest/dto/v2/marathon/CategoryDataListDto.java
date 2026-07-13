package app.oengus.adapter.rest.dto.v2.marathon;

import app.oengus.adapter.rest.dto.AbstractDataListDto;

import java.util.Collection;

public class CategoryDataListDto extends AbstractDataListDto<CategoryDto> {
    public CategoryDataListDto() {
        super();
    }

    public CategoryDataListDto(Collection<CategoryDto> data) {
        super(data);
    }
}
