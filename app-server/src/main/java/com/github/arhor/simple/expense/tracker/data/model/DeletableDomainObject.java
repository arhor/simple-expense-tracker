package com.github.arhor.simple.expense.tracker.data.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = true)
public abstract class DeletableDomainObject<T extends Serializable> extends DomainObject<T> {

    private boolean deleted;
}
