package mk.ukim.finki.wp.kino.exception;

public record FieldError(
        String field,
        String message) {}
