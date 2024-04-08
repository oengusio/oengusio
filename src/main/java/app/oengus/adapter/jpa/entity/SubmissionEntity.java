package app.oengus.adapter.jpa.entity;

import app.oengus.entity.comparator.AnswerComparator;
import app.oengus.adapter.rest.dto.v1.OpponentSubmissionDto;
import app.oengus.entity.model.*;
import app.oengus.helper.TimeHelpers;
import app.oengus.spring.model.Views;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Hibernate;
import org.hibernate.annotations.SortComparator;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import javax.persistence.*;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static app.oengus.helper.StringHelper.getUserDisplay;
import static javax.persistence.CascadeType.ALL;

@Getter
@Setter
@Entity
@Table(name = "submission")
public class SubmissionEntity {
    private static final List<String> DEFAULT_HEADERS =
        Arrays.asList("runner", "game_name", "game_description", "game_console", "game_ratio", "category_name",
            "category_description", "category_type", "category_estimate", "category_video", "status");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(Views.Public.class)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonView(Views.Public.class)
    private User user;

    @ManyToOne
    @JoinColumn(name = "marathon_id")
    @JsonBackReference(value = "marathonReference")
    @JsonView(Views.Public.class)
    private MarathonEntity marathon;

    @OneToMany(mappedBy = "submission", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id ASC")
    @JsonManagedReference
    @JsonView(Views.Public.class)
    private Set<GameEntity> games;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "availability", joinColumns = @JoinColumn(name = "submission_id"))
    @AttributeOverrides({
        @AttributeOverride(name = "from", column = @Column(name = "date_from")),
        @AttributeOverride(name = "to", column = @Column(name = "date_to"))
    })
    @OrderBy(value = "date_from ASC")
    @JsonView(Views.Public.class)
    private List<Availability> availabilities;

    @OneToMany(mappedBy = "submission", cascade = ALL, orphanRemoval = true)
    @JsonManagedReference(value = "answersReference")
    @SortComparator(AnswerComparator.class)
    @JsonView(Views.Public.class)
    private SortedSet<Answer> answers;

    @OneToMany(mappedBy = "submission", cascade = ALL, orphanRemoval = true)
    @JsonManagedReference(value = "opponentReference")
    @JsonView(Views.Public.class)
    private Set<OpponentEntity> opponents;

    @Transient
    @JsonView(Views.Public.class)
    private Set<OpponentSubmissionDto> opponentDtos;

    @JsonIgnore
    public String[] getCsvHeaders() {
        final List<String> headers = new ArrayList<>(DEFAULT_HEADERS);
        if (!CollectionUtils.isEmpty(this.getMarathon().getQuestions())) {
            this.getMarathon()
                .getQuestions()
                .stream()
                .filter((q) -> q.getFieldType() != FieldType.FREETEXT)
                .forEach(
                    (question) -> headers.add(question.getLabel())
                );
        }
        headers.add("availabilities");
        String[] array = new String[headers.size()];
        array = headers.toArray(array);
        return array;
    }

    @JsonIgnore
    public List<List<String>> getCsvRecords(final Locale locale, final String zoneId) {
        final List<List<String>> records = new ArrayList<>();
        final ResourceBundle resourceBundle = ResourceBundle.getBundle("export.Exports", locale);
        this.getGames().forEach(game -> game.getCategories().forEach(category -> {
            final List<String> record = new ArrayList<>();
            String opponents = null;
            if (category.getOpponents() != null) {
                opponents = category.getOpponents()
                    .stream()
                    .map(
                        opponent -> getUserDisplay(opponent.getSubmission().getUser())
                    )
                    .collect(Collectors.joining(", "));
            }
            record.add(
                getUserDisplay(this.user) +
                    (StringUtils.isEmpty(opponents) ? StringUtils.EMPTY : ", " + opponents));
            record.add(game.getName());
            record.add(StringUtils.normalizeSpace(game.getDescription()));
            record.add(game.getConsole() + (game.isEmulated() ? "*" : ""));
            record.add(game.getRatio());
            record.add(category.getName());
            record.add(StringUtils.normalizeSpace(category.getDescription()));
            record.add(resourceBundle.getString("run.type." + category.getType().name()));
            record.add(TimeHelpers.formatDuration(category.getEstimate()));
            if (category.getOpponents() == null || category.getOpponents().isEmpty()) {
                record.add(category.getVideo());
            } else {
                final StringBuilder videos = new StringBuilder(
                    getUserDisplay(this.user) + ": " + category.getVideo() + " - ");
                category.getOpponents().forEach(opponent -> {
                    videos.append(getUserDisplay(opponent.getSubmission().getUser()))
                        .append(":  ")
                        .append(opponent.getVideo());
                    if (category.getOpponents().indexOf(opponent) != category.getOpponents().size() - 1) {
                        videos.append(" - ");
                    }
                });
                record.add(videos.toString());
            }

            // Selection might be null, ensure that the status defaults to "TO-DO"
            final Selection selection = Optional.ofNullable(category.getSelection()).orElseGet(() -> {
                final Selection fakeSelection = new Selection();

                fakeSelection.setStatus(Status.TODO);

                return fakeSelection;
            });

            final String statusName = selection.getStatus().name();
            record.add(resourceBundle.getString("run.status." + statusName));
            if (!CollectionUtils.isEmpty(this.getAnswers())) {
                this.getAnswers()
                    .stream()
                    .filter((a) -> a.getQuestion().getFieldType() != FieldType.FREETEXT)
                    .forEach(
                        (answer) -> record.add(answer.getAnswer())
                    );
            }
            this.getAvailabilities().forEach(availability -> {
                record.add(DateTimeFormatter.ISO_ZONED_DATE_TIME.format(availability.getFrom().withZoneSameInstant(
                    ZoneId.of(zoneId))) + "/" +
                    DateTimeFormatter.ISO_ZONED_DATE_TIME.format(availability.getTo().withZoneSameInstant(
                        ZoneId.of(zoneId))));
            });
            records.add(record);
        }));
        return records;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubmissionEntity that = (SubmissionEntity) o;
        return id == that.id && user.equals(that.user) && marathon.equals(that.marathon) && games.equals(that.games);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, user, marathon, games, opponents);
    }

    @Deprecated(forRemoval = true)
    public SubmissionEntity fresh(boolean withGames) {
        final SubmissionEntity submission = new SubmissionEntity();

        // load all the items needed from the old submission
        Hibernate.initialize(this.getAvailabilities());
        Hibernate.initialize(this.getOpponents());
        Hibernate.initialize(this.getAnswers());

        // the games will be copied separately
        BeanUtils.copyProperties(this, submission, "games");

        // only load the game if we say so
        // might cause issues if we load the submission from a game otherwise
        if (withGames) {
            final Set<GameEntity> freshGames = new HashSet<>();

            this.getGames().forEach(
                (game) -> freshGames.add(game.fresh(false))
            );

            submission.setGames(freshGames);
        }

        return submission;
    }

    @Deprecated(forRemoval = true)
    public static void initialize(SubmissionEntity submission, boolean withGames) {
        // load all the items needed from the old submission
        Hibernate.initialize(submission.getAvailabilities());
        Hibernate.initialize(submission.getOpponents());
        Hibernate.initialize(submission.getAnswers());

        // only load the game if we say so
        // might cause issues if we load the submission from a game otherwise
        if (withGames) {
            submission.getGames().forEach(GameEntity::initialize);
        }
    }
}
