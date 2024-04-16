package app.oengus.domain;

import app.oengus.entity.model.Availability;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
public class Submission {
    private final int id;
    private final String marathonId;
    private final int userId;

    private List<Game> games = new ArrayList<>();
    private List<Availability> availabilities = new ArrayList<>();
}
