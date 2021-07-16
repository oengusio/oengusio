package app.oengus.entity.model;

import app.oengus.entity.constants.PatreonPledgeStatus;
import app.oengus.spring.model.Views;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cache;

import javax.annotation.Nullable;
import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "patreon_status")
@JsonIgnoreProperties(ignoreUnknown = true)
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class PatreonStatus {

    @Id
    @NotNull
    @Column(name = "patreon_id")
    @JsonView(Views.Public.class)
    private String patreonId;

    @Nullable
    @Column(name = "status")
    @Enumerated(EnumType.STRING) /* Default is ORDINAL */
    @JsonView(Views.Public.class)
    private PatreonPledgeStatus status;

    @NotNull
    @JsonView(Views.Public.class)
    @Column(name = "pledge_amount")
    private int pledgeAmount;

    public String getPatreonId() {
        return patreonId;
    }

    public void setPatreonId(String patreonId) {
        this.patreonId = patreonId;
    }

    @Nullable
    public PatreonPledgeStatus getStatus() {
        return status;
    }

    public void setStatus(@Nullable PatreonPledgeStatus status) {
        this.status = status;
    }

    public int getPledgeAmount() {
        return pledgeAmount;
    }

    public void setPledgeAmount(int pledgeAmount) {
        this.pledgeAmount = pledgeAmount;
    }
}
