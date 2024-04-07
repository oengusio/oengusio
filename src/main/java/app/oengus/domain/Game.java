package app.oengus.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class Game {
    private final int id;
    private final int submissionId;

    private String name;
    private String description;
    private String console;
    private String ratio;
    private boolean emulated;
}
