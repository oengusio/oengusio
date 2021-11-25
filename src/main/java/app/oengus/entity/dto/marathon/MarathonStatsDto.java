package app.oengus.entity.dto.marathon;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.time.Duration;
import java.util.Map;

@ApiModel(description = "Represents the stats of a marathon")
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

    @ApiModelProperty(value = "The amount of submissions, or runs, for this marathon.")
    public long getSubmissionCount() {
        return submissionCount;
    }

    @ApiModelProperty(value = "The amount of different runners that submitted.")
    public long getRunnerCount() {
        return runnerCount;
    }

    @ApiModelProperty(
        dataType = "String",
        value = "The total duration of all submitted runs. Formatted in the ISO-8601 duration format."
    )
    public String getTotalLength() {
        return totalLength.toString();
    }

    @ApiModelProperty(
        dataType = "String",
        value = "The average estimate if all runs combined. Formatted in the ISO-8601 duration format."
    )
    public String getAverageEstimate() {
        return averageEstimate.toString();
    }
}
