package app.oengus.entity.model;

import javax.annotation.Nullable;
import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;
import javax.validation.constraints.AssertTrue;

@Embeddable
public class ScheduleLineRunner {
    @Nullable
    @ManyToOne
    private User user;

    @Nullable
    private String runnerName = null;

    @Nullable
    public User getUser() {
        return user;
    }

    public ScheduleLineRunner setUser(@Nullable User runner) {
        this.user = runner;
        return this;
    }

    @Nullable
    public String getRunnerName() {
        return runnerName;
    }

    public ScheduleLineRunner setRunnerName(@Nullable String runnerName) {
        this.runnerName = runnerName;
        return this;
    }

    @AssertTrue
    public boolean runnerOrNameIsSet() {
        return this.user != null || this.runnerName != null;
    }
}
