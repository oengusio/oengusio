package app.oengus.domain.submission;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class Opponent {
    private final int id;
    private final int categoryId;

    private int userId;
    private int submissionId;
    private String video;
}
