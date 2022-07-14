package com.github.arhor.simple.expense.tracker.data.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public abstract class AuditableDomainObject<T extends Serializable> extends DeletableDomainObject<T> {
    @CreatedDate
    private LocalDateTime created;
    @LastModifiedDate
    private LocalDateTime updated;
}
