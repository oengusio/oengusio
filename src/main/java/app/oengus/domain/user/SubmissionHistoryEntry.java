package app.oengus.domain.user;

import app.oengus.domain.marathon.Marathon;
import app.oengus.domain.submission.Game;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class SubmissionHistoryEntry {
    private Marathon marathon;
    private Set<Game> games = new HashSet<>();
}
