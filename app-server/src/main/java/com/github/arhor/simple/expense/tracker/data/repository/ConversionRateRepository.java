package com.github.arhor.simple.expense.tracker.data.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.github.arhor.simple.expense.tracker.data.model.ConversionRate;

@Repository
public interface ConversionRateRepository extends CrudRepository<ConversionRate, ConversionRate.Id> {
}
