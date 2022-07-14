package com.github.arhor.simple.expense.tracker.data.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public abstract class DeletableDomainObject<T extends Serializable> extends DomainObject<T> {
    private boolean deleted;
}
