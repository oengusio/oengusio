package app.oengus.entity.dto;

import app.oengus.domain.PatreonPledgeStatus;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class PatreonStatusDto {

    @NotNull
    @NotBlank
    private String patreonId;

    @NotNull
    private PatreonPledgeStatus status;

    @Min(value = 0)
    @NotNull
    private int pledgeAmount;

    public String getPatreonId() {
        return patreonId;
    }

    public void setPatreonId(String patreonId) {
        this.patreonId = patreonId;
    }

    public PatreonPledgeStatus getStatus() {
        return status;
    }

    public void setStatus(PatreonPledgeStatus status) {
        this.status = status;
    }

    public int getPledgeAmount() {
        return pledgeAmount;
    }

    public void setPledgeAmount(int pledgeAmount) {
        this.pledgeAmount = pledgeAmount;
    }
}
