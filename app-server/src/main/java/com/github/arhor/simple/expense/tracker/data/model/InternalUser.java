package com.github.arhor.simple.expense.tracker.data.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import org.springframework.data.relational.core.mapping.Table;

@Data
@Table("users")
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class InternalUser extends AuditableDomainObject<Long> {
    private String username;
    private String password;
    private String currency;
    private String externalId;
    private String externalProvider;
}
