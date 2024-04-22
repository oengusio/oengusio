package app.oengus.domain.schedule;

import app.oengus.domain.OengusUser;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Runner {
    private String runnerName;

    private OengusUser user;
}
