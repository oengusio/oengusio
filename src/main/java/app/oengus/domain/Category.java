package app.oengus.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
public class Category {
    private final int id;
    private final int gameId;

    private String name;
    private Duration estimate;
    private String description;
    private String video;
    private RunType type;
    private String code;

    // Runner and opponents
    private List<OengusUser> runners = new ArrayList<>();
}
