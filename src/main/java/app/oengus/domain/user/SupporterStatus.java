package app.oengus.domain.user;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class SupporterStatus {
    private boolean sponsor;
    private boolean patron;
}
