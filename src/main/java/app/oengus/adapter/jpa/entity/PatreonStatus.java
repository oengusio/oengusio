package app.oengus.adapter.jpa.entity;

import app.oengus.domain.PatreonPledgeStatus;
import app.oengus.spring.model.Views;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nullable;
import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@Entity
@Table(name = "patreon_status")
@JsonIgnoreProperties(ignoreUnknown = true)
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
}
