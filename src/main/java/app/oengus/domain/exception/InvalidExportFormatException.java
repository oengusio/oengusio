package app.oengus.domain.exception;

public class InvalidExportFormatException extends RuntimeException {
    public InvalidExportFormatException(String format) {
        super(
            "Your chosen format '%s' is not supported.".formatted(format)
        );
    }
}
