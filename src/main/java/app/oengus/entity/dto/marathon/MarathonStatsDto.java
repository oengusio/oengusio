package app.oengus.entity.dto.marathon;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Duration;
import java.util.Map;

@Schema(description = "Represents the stats of a marathon")
public class MarathonStatsDto {
    private final long submissionCount;
    private final long runnerCount;
    private final Duration totalLength;
    private final Duration averageEstimate;

    public MarathonStatsDto(Map<String, Object> items) {
        this(
            Long.parseLong(items.get("submissionCount").toString()),
            Long.parseLong(items.get("runnerCount").toString()),
            Long.parseLong(items.get("totalLength").toString()),
            Double.parseDouble(items.get("averageEstimate").toString())
        );
    }

    public MarathonStatsDto(long submissionCount, long runnerCount, long totalLength, double averageEstimate) {
        this.submissionCount = submissionCount;
        this.runnerCount = runnerCount;
        this.totalLength = Duration.ofNanos(totalLength);
        // this works fine
        this.averageEstimate = Duration.ofNanos((long) averageEstimate);
    }

    @Schema(description = "The amount of submissions, or runs, for this marathon.")
    public long getSubmissionCount() {
        return submissionCount;
    }

    @Schema(description = "The amount of different runners that submitted.")
    public long getRunnerCount() {
        return runnerCount;
    }

    @Schema(
        description = "The total duration of all submitted runs. Formatted in the ISO-8601 duration format."
    )
    public String getTotalLength() {
        return totalLength.toString();
    }

    @Schema(
        description = "The average estimate if all runs combined. Formatted in the ISO-8601 duration format."
    )
    public String getAverageEstimate() {
        return averageEstimate.toString();
    }
}
