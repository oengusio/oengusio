package app.oengus.domain.submission;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.annotation.Nullable;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
@RequiredArgsConstructor
public class Category {
    public static final int NAME_MAX_LENGTH = 100;
    public static final int DESCRIPTION_MAX_LENGTH = 300;
    public static final int VIDEO_MAX_LENGTH = 100;

    private final int id;
    private final int gameId;

    private String name;
    private Duration estimate;
    private String description;
    private String video;
    private RunType type;
    private String code;
    private int expectedRunnerCount;

    private ZonedDateTime createdAt;

    @Nullable
    private Selection selection;

    private List<Opponent> opponents = new ArrayList<>();
}
