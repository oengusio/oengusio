package app.oengus.adapter.jpa.entity;

import app.oengus.domain.submission.RunType;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.time.DurationMin;

import javax.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "schedule_line")
public class ScheduleLine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "schedule_id")
    private ScheduleEntity schedule;

    @Column(name = "game_name")
    @Size(max = 100)
    private String gameName;

    @Column(name = "console")
    @Size(max = 45) // can grow upto 100 due to db type being varchar(100)
    private String console;

    @Column(name = "emulated")
    private boolean emulated = false;

    @Column(name = "ratio")
    @Size(max = 10)
    private String ratio;

    @Column(name = "category_name")
    @Size(max = 100)
    private String categoryName;

    @Column(name = "estimate")
    @NotNull
    @DurationMin(seconds = 0)
    private Duration estimate;

    @Column(name = "setup_time")
    @DurationMin(seconds = 0)
    private Duration setupTime;

    @Column(name = "setup_block")
    private boolean setupBlock;

    @Column(name = "custom_run")
    private boolean customRun;

    @Column(name = "position")
    private int position;

    @Column(name = "category_id")
    @Nullable
    private Integer categoryId;

    @Column(name = "run_type")
    private RunType type;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "schedule_line_runner",
        joinColumns = {@JoinColumn(name = "schedule_line_id")}
    )
    @AttributeOverrides({
        @AttributeOverride(name = "user", column = @Column(name = "user_id")),
        @AttributeOverride(name = "runnerName", column = @Column(name = "runner_name"))
    })
    private List<ScheduleLineRunner> runners;

    @Column(name = "setup_block_text")
    @Size(max = 50)
    private String setupBlockText;

    @Transient
    private ZonedDateTime date;

    @Column(name = "custom_data")
    private String customData;

    @Transient
    private String customDataDTO;
}
