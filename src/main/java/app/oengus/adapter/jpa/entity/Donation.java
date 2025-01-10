package app.oengus.adapter.jpa.entity;

import app.oengus.adapter.jpa.entity.comparator.DonationExtraDataComparator;
import app.oengus.domain.marathon.FieldType;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SortComparator;

import jakarta.persistence.*;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Pattern;
import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

@Getter
@Setter
@Entity
@Table(name = "donation")
public class Donation {

    private static final List<String> DEFAULT_HEADERS = List.of("date", "nickname", "amount", "comment");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "marathon_id")
    private MarathonEntity marathon;

    @Column(name = "functional_id")
    private String functionalId;

    @Column(name = "payment_source")
    private String paymentSource;

    @Column(name = "nickname")
    @Pattern(regexp = "^[\\w\\-]{0,16}$")
    private String nickname;

    @Column(name = "donation_date")
    private ZonedDateTime date;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "donation_comment")
    private String comment;

    @Column(name = "approved")
    private boolean approved;

    @OneToMany(mappedBy = "donation", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<DonationIncentiveLink> donationIncentiveLinks;

    @OneToMany(mappedBy = "donation", cascade = CascadeType.ALL, orphanRemoval = true)
    @SortComparator(DonationExtraDataComparator.class)
    private SortedSet<DonationExtraData> answers;

    @AssertTrue
    public boolean isIncentiveTotalInferiorToAmount() {
        if (this.donationIncentiveLinks == null || this.donationIncentiveLinks.isEmpty()) {
            return true;
        }

        return this.amount.compareTo(
            this.donationIncentiveLinks.stream()
                .map(DonationIncentiveLink::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
        ) >= 0;
    }


    // TODO: export to different class
    public String[] getCsvHeaders() {
        final List<String> headers = new ArrayList<>(DEFAULT_HEADERS);
        this.getAnswers()
            .stream()
            .filter(answer -> !answer.getQuestion().getFieldType().equals(FieldType.FREETEXT))
            .forEach(answer ->
                headers.add(answer.getQuestion().getLabel()));
        String[] array = new String[headers.size()];
        array = headers.toArray(array);
        return array;
    }

    public List<List<String>> getCsvRecords(final String zoneId) {
        final List<String> record = new ArrayList<>();
        record.add(DateTimeFormatter.ISO_ZONED_DATE_TIME.format(this.date.withZoneSameInstant(
            ZoneId.of(zoneId))));
        record.add(this.nickname);
        record.add(this.amount.toPlainString());
        record.add(this.comment);
        this.getAnswers()
            .stream()
            .filter(answer -> !answer.getQuestion().getFieldType().equals(FieldType.FREETEXT))
            .forEach(answer -> record.add(answer.getAnswer()));
        return List.of(record);
    }
}
