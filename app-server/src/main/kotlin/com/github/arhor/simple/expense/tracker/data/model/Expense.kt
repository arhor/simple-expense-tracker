package com.github.arhor.simple.expense.tracker.data.model

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Immutable
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Immutable
@Table(name = "expenses")
data class Expense(
    @Id
    val id: Long? = null,
    val userId: Long,
    val name: String,
    val icon: String?,
    val color: String?,
    @CreatedDate
    val created: LocalDateTime? = null,
    @LastModifiedDate
    val updated: LocalDateTime? = null,
)
