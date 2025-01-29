package com.github.arhor.simple.expense.tracker.api.error;

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import java.time.temporal.Temporal

@JsonPropertyOrder(
    "code",
    "message",
    "details",
    "timestamp",
    "requestId",
)
@JsonInclude(JsonInclude.Include.NON_NULL)
data class ErrorResponse(
    val requestId: String,
    val message: String,
    val timestamp: Temporal,
    val code: ErrorCode,
    val details: List<String>,
)
