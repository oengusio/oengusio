package app.oengus.domain.marathon;

import java.math.BigDecimal;

public record MarathonStats(
    long submissionCount,
    long runnerCount,
    long totalLength,
    double averageEstimate
) {
}
