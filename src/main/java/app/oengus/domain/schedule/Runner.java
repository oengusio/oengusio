package app.oengus.domain.schedule;

import app.oengus.domain.OengusUser;
import app.oengus.application.helper.StringHelper;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Runner {
    private String runnerName;

    private OengusUser user;

    public String getEffectiveDisplay() {
        if (this.user == null) {
            return this.runnerName;
        }

        return StringHelper.getUserDisplay(this.user);
    }
}
