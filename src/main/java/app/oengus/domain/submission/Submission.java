package app.oengus.domain.submission;

import app.oengus.domain.OengusUser;
import app.oengus.entity.model.Availability;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
@RequiredArgsConstructor
public class Submission {
    private final int id;
    private final String marathonId;
//    private final int userId;

    private OengusUser user;

    private List<Opponent> opponents;

    // TODO: do we need to make this sorted?
    private Set<Answer> answers = new HashSet<>();
    private Set<Game> games = new HashSet<>();
    private List<Availability> availabilities = new ArrayList<>();
}
