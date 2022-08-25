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
    val id: Long? = null,
    val username: String? = null,
    val password: String? = null,
    val currency: String,
    val externalId: String? = null,
    val externalProvider: String? = null,
    @CreatedDate
    val created: LocalDateTime? = null,
    @LastModifiedDate
    val updated: LocalDateTime? = null,
)
