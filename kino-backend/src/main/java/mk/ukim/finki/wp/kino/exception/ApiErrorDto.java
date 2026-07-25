package mk.ukim.finki.wp.kino.exception;

import java.time.Instant;
import java.util.List;

public record ApiErrorDto(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path,
        List<FieldError> fieldErrors) {

    public ApiErrorDto(Instant timestamp, int status, String error, String message, String path) {
        this(timestamp, status, error, message, path, null);
    }
}
