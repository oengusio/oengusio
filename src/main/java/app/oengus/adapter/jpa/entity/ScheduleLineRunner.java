package app.oengus.adapter.jpa.entity;

import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nullable;
import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;
import javax.validation.constraints.AssertTrue;

@Getter
@Setter
@Embeddable
public class ScheduleLineRunner {
    @Nullable
    @ManyToOne
    private User user;

    @Nullable
    private String runnerName = null;

    @AssertTrue
    public boolean runnerOrNameIsSet() {
        return this.user != null || this.runnerName != null;
    }
}
