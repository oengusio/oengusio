package app.oengus.entity.dto;

import app.oengus.domain.PatreonPledgeStatus;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

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
