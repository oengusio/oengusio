package app.oengus.domain.submission;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
public class Game {
    public static final int NAME_MAX_LENGTH = 100;
    public static final int DESCRIPTION_MAX_LENGTH = 500;
    public static final int CONSOLE_MAX_LENGTH = 45; // can grow upto 100 due to db type being varchar(100)
    public static final int RATIO_MAX_LENGTH = 10;

    private final int id;
    private final int submissionId;

    private String name;
    private String description;
    private String console;
    private String ratio;
    private boolean emulated;

    private List<Category> categories = new ArrayList<>();
}
