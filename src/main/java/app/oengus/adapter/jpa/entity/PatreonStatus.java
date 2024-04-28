package app.oengus.adapter.jpa.entity;

import app.oengus.domain.PatreonPledgeStatus;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nullable;
import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@Entity
@Table(name = "patreon_status")
public class PatreonStatus {

    @Id
    @NotNull
    @Column(name = "patreon_id")
    private String patreonId;

    @Nullable
    @Column(name = "status")
    @Enumerated(EnumType.STRING) /* Default is ORDINAL */
    private PatreonPledgeStatus status;

    @NotNull
    @Column(name = "pledge_amount")
    private int pledgeAmount;
}
