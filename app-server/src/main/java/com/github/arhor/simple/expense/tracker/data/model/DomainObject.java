package com.github.arhor.simple.expense.tracker.data.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

import org.springframework.data.annotation.Id;

/**
 * Base class for any entity used in the application.
 *
 * @param <T> primary key type
 */
@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public abstract class DomainObject<T extends Serializable> implements Serializable {
    @Id
    private T id;
}
