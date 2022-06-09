package com.github.arhor.simple.expense.tracker.web.error;

import java.io.Serializable;
import java.time.Instant;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
    "code",
    "message",
    "details",
    "timestamp",
    "requestId",
})
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
    String requestId,
    String message,
    Instant timestamp,
    ErrorCode code,
    List<String> details
) implements Serializable {

    public ErrorResponse(final String requestId, final String message, final Instant timestamp, final ErrorCode code) {
        this(requestId, message, timestamp, code, Collections.emptyList());
    }

    public ErrorResponse(final String requestId, final String message, final Instant timestamp) {
        this(requestId, message, timestamp, ErrorCode.UNAUTHORIZED);
    }
}
