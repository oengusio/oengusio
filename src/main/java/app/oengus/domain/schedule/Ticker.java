package app.oengus.domain.schedule;

public record Ticker(
    Line previous,
    Line current,
    Line next
) {
}
