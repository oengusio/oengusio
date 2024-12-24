package app.oengus.adapter.jpa.entity;

import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nullable;
import jakarta.persistence.Embeddable;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.AssertTrue;

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
