package app.oengus.adapter.jpa.entity;

import app.oengus.domain.submission.RunType;
import app.oengus.adapter.rest.Views;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.time.DurationMin;

import javax.annotation.Nullable;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "schedule_line")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ScheduleLine {
    // TODO: unique key out of schedule id + position?
    @Id
    @JsonView(Views.Public.class)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "schedule_id")
    @JsonBackReference
    @JsonView(Views.Public.class)
    private ScheduleEntity schedule;

    @Column(name = "game_name")
    @JsonView(Views.Public.class)
    @Size(max = 100)
    private String gameName;

    @Column(name = "console")
    @JsonView(Views.Public.class)
    @Size(max = 45) // can grow upto 100 due to db type being varchar(100)
    private String console;

    @Column(name = "emulated")
    @JsonView(Views.Public.class)
    private boolean emulated = false;

    @Column(name = "ratio")
    @JsonView(Views.Public.class)
    @Size(max = 10)
    private String ratio;

    @Column(name = "category_name")
    @JsonView(Views.Public.class)
    @Size(max = 100)
    private String categoryName;

    @Column(name = "estimate")
    @JsonView(Views.Public.class)
    @NotNull
    @DurationMin(seconds = 0)
    private Duration estimate;

    @Column(name = "setup_time")
    @JsonView(Views.Public.class)
    @DurationMin(seconds = 0)
    private Duration setupTime;

    @Column(name = "setup_block")
    @JsonView(Views.Public.class)
    private boolean setupBlock;

    @Column(name = "custom_run")
    @JsonView(Views.Public.class)
    private boolean customRun;

    @Column(name = "position")
    @JsonView(Views.Public.class)
    private int position;

    @Column(name = "category_id")
    @JsonView(Views.Public.class)
    @Nullable
    private Integer categoryId;

    @Column(name = "run_type")
    @JsonView(Views.Public.class)
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
    @JsonView(Views.Public.class)
    private List<ScheduleLineRunner> runners;

    @Column(name = "setup_block_text")
    @JsonView(Views.Public.class)
    @Size(max = 50)
    private String setupBlockText;

    @Transient
    private ZonedDateTime date;

    @Column(name = "custom_data")
    @JsonView(Views.Internal.class)
    private String customData;

    @Transient
    @JsonView(Views.Public.class)
    private String customDataDTO;
}
