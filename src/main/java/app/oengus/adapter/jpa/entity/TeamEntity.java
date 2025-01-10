package app.oengus.adapter.jpa.entity;

import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.ZonedDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "teams")
public class TeamEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @JoinColumn(name = "marathon_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private MarathonEntity marathon;

    @NotNull
    @NotBlank
    @Size(max = 100)
    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @NotNull
    @Size(min = 1)
    @Column(name = "team_size")
    private int teamSize;

    @NotNull
    @Column(name = "applications_open")
    private boolean applicationsOpen;

    @Nullable
    @Column(name = "application_open_date")
    private ZonedDateTime applicationOpenDate;

    @Nullable
    @Column(name = "application_close_date")
    private ZonedDateTime applicationCloseDate;

    @ManyToMany
    @JoinTable(
        name = "team_leaders",
        joinColumns = {@JoinColumn(name = "team_id")},
        inverseJoinColumns = {@JoinColumn(name = "user_id")}
    )
    @OrderBy(value = "id ASC")
    private List<User> leaders;

    public static TeamEntity ofId(int id) {
        final var team = new TeamEntity();

        team.setId(id);

        return team;
    }
}
