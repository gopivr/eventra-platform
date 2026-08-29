package app.eventra.platform.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.net.URI;
import java.time.Instant;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ProblemDetailResponse(
    URI type,
    String title,
    int status,
    String code,
    String detail,
    String instance,
    String traceId,
    Instant timestamp) {}
