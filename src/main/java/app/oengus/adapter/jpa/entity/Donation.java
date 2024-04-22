package app.oengus.adapter.jpa.entity;

import app.oengus.adapter.jpa.entity.comparator.DonationExtraDataComparator;
import app.oengus.adapter.rest.Views;
import com.fasterxml.jackson.annotation.*;
import org.hibernate.annotations.SortComparator;

import javax.persistence.*;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Pattern;
import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

@Entity
@Table(name = "donation")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Donation {

    private static final List<String> DEFAULT_HEADERS = List.of("date", "nickname", "amount", "comment");

    @Id
    @JsonView(Views.Public.class)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "marathon_id")
    @JsonBackReference
    @JsonView(Views.Public.class)
    private MarathonEntity marathon;

    @Column(name = "functional_id")
    @JsonView(Views.Internal.class)
    private String functionalId;

    @Column(name = "payment_source")
    @JsonView(Views.Internal.class)
    private String paymentSource;

    @Column(name = "nickname")
    @Pattern(regexp = "^[\\w\\-]{0,16}$")
    @JsonView(Views.Public.class)
    private String nickname;

    @Column(name = "donation_date")
    @JsonView(Views.Public.class)
    private ZonedDateTime date;

    @Column(name = "amount")
    @JsonView(Views.Public.class)
    private BigDecimal amount;

    @Column(name = "donation_comment")
    @JsonView(Views.Public.class)
    private String comment;

    @Column(name = "approved")
    @JsonView(Views.Internal.class)
    private boolean approved;

    @OneToMany(mappedBy = "donation", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonView(Views.Public.class)
    @JsonManagedReference("donation")
    private Set<DonationIncentiveLink> donationIncentiveLinks;

    @OneToMany(mappedBy = "donation", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference(value = "donationReference")
    @SortComparator(DonationExtraDataComparator.class)
    @JsonView(Views.Public.class)
    private SortedSet<DonationExtraData> answers;

    @AssertTrue
    @JsonIgnore
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

    public String getFunctionalId() {
        return this.functionalId;
    }

    public void setFunctionalId(final String functionalId) {
        this.functionalId = functionalId;
    }

    public String getPaymentSource() {
        return this.paymentSource;
    }

    public void setPaymentSource(final String paymentSource) {
        this.paymentSource = paymentSource;
    }

    public String getNickname() {
        return this.nickname;
    }

    public void setNickname(final String nickname) {
        this.nickname = nickname;
    }

    public ZonedDateTime getDate() {
        return this.date;
    }

    public void setDate(final ZonedDateTime date) {
        this.date = date;
    }

    public BigDecimal getAmount() {
        return this.amount;
    }

    public void setAmount(final BigDecimal amount) {
        this.amount = amount;
    }

    public String getComment() {
        return this.comment;
    }

    public void setComment(final String comment) {
        this.comment = comment;
    }

    public boolean getApproved() {
        return this.approved;
    }

    public void setApproved(final boolean approved) {
        this.approved = approved;
    }

    public int getId() {
        return this.id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public MarathonEntity getMarathon() {
        return this.marathon;
    }

    public void setMarathon(final MarathonEntity marathon) {
        this.marathon = marathon;
    }

    public Set<DonationIncentiveLink> getDonationIncentiveLinks() {
        return this.donationIncentiveLinks;
    }

    public void setDonationIncentiveLinks(final Set<DonationIncentiveLink> donationIncentiveLinks) {
        this.donationIncentiveLinks = donationIncentiveLinks;
    }

    public SortedSet<DonationExtraData> getAnswers() {
        return this.answers;
    }

    public void setAnswers(final SortedSet<DonationExtraData> answers) {
        this.answers = answers;
    }

    @JsonIgnore
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

    @JsonIgnore
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
