package com.github.arhor.simple.expense.tracker.data.model;

import lombok.Data;

import java.io.Serializable;

import org.springframework.data.annotation.Id;

/**
 * Base class for any entity used in the application.
 *
 * @param <T> primary key type
 */
@Data
public abstract class DomainObject<T extends Serializable> implements Serializable {

    @Id
    private T id;
}
