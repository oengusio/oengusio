package app.oengus.domain.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.Duration;

@Getter
@Setter
@RequiredArgsConstructor
public class SavedCategory {
    private final int id;
    private final int gameId;

    private String name;
    private String description;
    private Duration estimate;
    private String video;
}
