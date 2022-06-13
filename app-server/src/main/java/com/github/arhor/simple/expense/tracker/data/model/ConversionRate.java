package com.github.arhor.simple.expense.tracker.data.model;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.data.relational.core.mapping.Table;

@Data
@Table("conversion_rates")
public class ConversionRate {

    private String baseCurrency;
    private String termCurrency;
    private LocalDate date;
    private BigDecimal rate;

    public record Id(String baseCurrency, String termCurrency, LocalDate date) implements Serializable {
    }
}
