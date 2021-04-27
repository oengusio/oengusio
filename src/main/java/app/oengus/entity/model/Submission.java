package app.oengus.entity.model;

import app.oengus.entity.comparator.AnswerComparator;
import app.oengus.entity.dto.OpponentSubmissionDto;
import app.oengus.helper.TimeHelpers;
import app.oengus.spring.model.Views;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonView;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Hibernate;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.SortComparator;
import org.springframework.util.CollectionUtils;

import javax.persistence.*;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Table(name = "submission")
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Submission {
    private static final List<String> DEFAULT_HEADERS =
        Arrays.asList("runner", "game_name", "game_description", "game_console", "game_ratio", "category_name",
            "category_description", "category_type", "category_estimate", "category_video", "status");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(Views.Public.class)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonView(Views.Public.class)
    private User user;

    @ManyToOne
    @JoinColumn(name = "marathon_id")
    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonBackReference(value = "marathonReference")
    @JsonView(Views.Public.class)
    private Marathon marathon;

    @OneToMany(mappedBy = "submission", cascade = CascadeType.ALL, orphanRemoval = true)
    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @OrderBy("id ASC")
    @JsonManagedReference
    @JsonView(Views.Public.class)
    private Set<Game> games;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "availability", joinColumns = @JoinColumn(name = "submission_id"))
    @AttributeOverrides({
        @AttributeOverride(name = "from", column = @Column(name = "date_from")),
        @AttributeOverride(name = "to", column = @Column(name = "date_to"))
    })
    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @OrderBy(value = "date_from ASC")
    @JsonView(Views.Public.class)
    private List<Availability> availabilities;

    @OneToMany(mappedBy = "submission", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference(value = "answersReference")
    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @SortComparator(AnswerComparator.class)
    @JsonView(Views.Public.class)
    private SortedSet<Answer> answers;

    @OneToMany(mappedBy = "submission", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference(value = "opponentReference")
    @JsonView(Views.Public.class)
    private Set<Opponent> opponents;

    @Transient
    @JsonView(Views.Public.class)
    private Set<OpponentSubmissionDto> opponentDtos;

    public Integer getId() {
        return this.id;
    }

    public void setId(final Integer id) {
        this.id = id;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(final User user) {
        this.user = user;
    }

    public Marathon getMarathon() {
        return this.marathon;
    }

    public void setMarathon(final Marathon marathon) {
        this.marathon = marathon;
    }

    public List<Availability> getAvailabilities() {
        return this.availabilities;
    }

    public void setAvailabilities(final List<Availability> availabilities) {
        this.availabilities = availabilities;
    }

    public Set<Game> getGames() {
        return this.games;
    }

    public void setGames(final Set<Game> games) {
        this.games = games;
    }

    public SortedSet<Answer> getAnswers() {
        return this.answers;
    }

    public void setAnswers(final SortedSet<Answer> answers) {
        this.answers = answers;
    }

    public Set<Opponent> getOpponents() {
        return this.opponents;
    }

    public void setOpponents(final Set<Opponent> opponents) {
        this.opponents = opponents;
    }

    public Set<OpponentSubmissionDto> getOpponentDtos() {
        return this.opponentDtos;
    }

    public void setOpponentDtos(final Set<OpponentSubmissionDto> opponentDtos) {
        this.opponentDtos = opponentDtos;
    }

    @JsonIgnore
    public String[] getCsvHeaders() {
        final List<String> headers = new ArrayList<>(DEFAULT_HEADERS);
        if (!CollectionUtils.isEmpty(this.getMarathon().getQuestions())) {
            this.getMarathon().getQuestions().forEach(question -> {
                headers.add(question.getLabel());
            });
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
                        opponent -> opponent.getSubmission().getUser().getUsername(locale.toLanguageTag())
                    )
                    .collect(Collectors.joining(", "));
            }
            record.add(
                this.user.getUsername(locale.toLanguageTag()) +
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
                    this.user.getUsername(locale.toLanguageTag()) + ": " + category.getVideo() + " - ");
                category.getOpponents().forEach(opponent -> {
                    videos.append(opponent.getSubmission().getUser().getUsername(locale.toLanguageTag()))
                        .append(":  ")
                        .append(opponent.getVideo());
                    if (category.getOpponents().indexOf(opponent) != category.getOpponents().size() - 1) {
                        videos.append(" - ");
                    }
                });
                record.add(videos.toString());
            }
            final String statusName = category.getSelection().getStatus().name();
            record.add(resourceBundle.getString("run.status." + statusName));
            if (!CollectionUtils.isEmpty(this.getAnswers())) {
                this.getAnswers().forEach(answer -> record.add(answer.getAnswer()));
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
        Submission that = (Submission) o;
        return id.equals(that.id) && user.equals(that.user) && marathon.equals(that.marathon) && games.equals(that.games);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, user, marathon, games, opponents);
    }

    public static void initialize(Submission submission, boolean withGames) {
        // load all the items needed from the old submission
        Hibernate.initialize(submission.getAvailabilities());
        Hibernate.initialize(submission.getOpponents());
        Hibernate.initialize(submission.getAnswers());

        // only load the game if we say so
        // might cause issues if we load the submission from a game otherwise
        if (withGames) {
            submission.getGames().forEach(Game::initialize);
        }
    }
}
