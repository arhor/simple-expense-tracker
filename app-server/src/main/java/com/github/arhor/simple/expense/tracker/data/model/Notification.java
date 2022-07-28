package com.github.arhor.simple.expense.tracker.data.model;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Immutable;
import org.springframework.data.relational.core.mapping.Table;

@Table("notifications")
@Builder(toBuilder = true)
@Immutable
public record Notification(
    @Id
    UUID id,
    String message,
    String severity,
    Long sourceUserId,
    Long targetUserId,
    LocalDateTime timestamp
) {

    /**
     * Compact projection of the {@link Notification} entity. Main purpose is to load only necessary fields from DB.
     */
    public record CompactProjection(UUID id, Long targetUserId, String message, String severity) {
    }
}
