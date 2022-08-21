package com.github.arhor.simple.expense.tracker.data.model;

import lombok.Builder
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Immutable
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("users")
@Builder(toBuilder = true)
@Immutable
data class InternalUser(
    @Id
    val id: Long,
    val username: String,
    val password: String,
    val currency: String,
    val externalId: String,
    val externalProvider: String,
    @CreatedDate
    val created: LocalDateTime?,
    @LastModifiedDate
    val updated: LocalDateTime?,
)
