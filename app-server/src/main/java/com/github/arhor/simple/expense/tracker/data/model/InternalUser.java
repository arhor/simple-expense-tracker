package com.github.arhor.simple.expense.tracker.data.model;

import lombok.Builder;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Immutable;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Table;

@Table("users")
@Builder(toBuilder = true)
@Immutable
public record InternalUser(
    @Id
    Long id,
    String username,
    String password,
    String currency,
    String externalId,
    String externalProvider,
    @CreatedDate
    LocalDateTime created,
    @LastModifiedDate
    LocalDateTime updated
) {

    /**
     * Compact projection of the {@link InternalUser} entity. Main purpose is to load only necessary fields from DB.
     */
    public record CompactProjection(Long id, String username, String currency) {
    }
}
