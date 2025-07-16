package app.oengus.domain.user;

import lombok.*;

import java.time.Duration;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@RequiredArgsConstructor
public class SavedCategory {
    private final int id;
    private final int gameId;

    private String name;
    private String description;
    private Duration estimate;
    private String video;
}
