package app.oengus.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class PledgeInfo {
    private final String patreonId;

    private PatreonPledgeStatus status;
    private int pledgeAmount;
}
