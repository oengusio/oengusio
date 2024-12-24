package app.oengus.adapter.rest.dto;

import app.oengus.domain.PatreonPledgeStatus;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Getter
@Setter
public class PatreonStatusDto {

    @NotNull
    @NotBlank
    private String patreonId;

    @NotNull
    private PatreonPledgeStatus status;

    @Min(value = 0)
    @NotNull
    private int pledgeAmount;
}
