package mk.ukim.finki.wp.kino.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.RestClientResponseException;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(RestClientResponseException.class)
    public ResponseEntity<ApiErrorDto> handleRestClientResponse(RestClientResponseException ex, HttpServletRequest request){
        int status = ex.getStatusCode().value();

        ApiErrorDto body = new ApiErrorDto(
                Instant.now(),
                status,
                ex.getStatusText(),
                "Upstream TMDB error",
                request.getRequestURI()
        );
        return ResponseEntity.status(status).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorDto> handleGeneric(Exception ex, HttpServletRequest request){
        ApiErrorDto body = new ApiErrorDto(
                Instant.now(),
                500,
                "Internal server error",
                ex.getMessage() == null ? "Unexpected error" : ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
