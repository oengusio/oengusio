package app.oengus.domain.submission;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class Opponent {
    private final int id;
    private final int categoryId;

    private Submission submission;
    private String video;
}
