package com.github.arhor.simple.expense.tracker.web.error;

import java.io.Serializable;
import java.time.temporal.Temporal;
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
    Temporal timestamp,
    ErrorCode code,
    List<String> details
) implements Serializable {
}
