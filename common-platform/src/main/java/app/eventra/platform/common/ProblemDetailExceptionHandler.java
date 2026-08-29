package app.eventra.platform.common;

import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import java.time.Instant;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ProblemDetailExceptionHandler {
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ProblemDetailResponse> validation(
      MethodArgumentNotValidException exception, HttpServletRequest request) {
    return problem(
        HttpStatus.UNPROCESSABLE_ENTITY, "VALIDATION_FAILED", "Request validation failed", request);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ProblemDetailResponse> unexpected(
      Exception exception, HttpServletRequest request) {
    return problem(
        HttpStatus.INTERNAL_SERVER_ERROR,
        "INTERNAL_ERROR",
        "An unexpected error occurred",
        request);
  }

  private ResponseEntity<ProblemDetailResponse> problem(
      HttpStatus status, String code, String detail, HttpServletRequest request) {
    ProblemDetailResponse body =
        new ProblemDetailResponse(
            URI.create("https://api.eventra.app/problems/" + code.toLowerCase()),
            status.getReasonPhrase(),
            status.value(),
            code,
            detail,
            request.getRequestURI(),
            request.getHeader("X-Correlation-ID"),
            Instant.now());
    return ResponseEntity.status(status).contentType(MediaType.APPLICATION_PROBLEM_JSON).body(body);
  }
}
