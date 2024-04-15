package app.oengus.domain.marathon;

public record MarathonStats(
    long submissionCount,
    long runnerCount,
    long totalLength,
    double averageEstimate
) {
}
