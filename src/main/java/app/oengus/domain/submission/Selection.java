package app.oengus.domain.submission;

import app.oengus.entity.model.Status;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class Selection {
    private final int id;
    private String marathonId;
    private final int categoryId;

    private Status status;
}
