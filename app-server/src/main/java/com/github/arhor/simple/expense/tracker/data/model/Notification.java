package com.github.arhor.simple.expense.tracker.data.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.relational.core.mapping.Table;

@Data
@Table("notifications")
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
public class Notification extends DomainObject<UUID> {
    private Long userId;
    private String message;
    private String severity;
    private LocalDateTime timestamp;
    private Long createdBy;

    /**
     * Compact projection of the {@link Notification} entity. Main purpose is to load only necessary fields from DB.
     */
    public record CompactProjection(UUID id, Long userId, String message, String severity) {
    }
}
