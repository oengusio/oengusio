package app.oengus.domain.schedule;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
public class Schedule {
    private final int id;
    private String marathonId;

    private String name;
    private String slug;
    private boolean published;

    private List<Line> lines = new ArrayList<>();
}
