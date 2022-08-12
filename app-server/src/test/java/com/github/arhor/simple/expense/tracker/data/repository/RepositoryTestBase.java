package com.github.arhor.simple.expense.tracker.data.repository;

import lombok.val;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import org.junit.jupiter.api.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.github.arhor.simple.expense.tracker.config.DatabaseConfig;
import com.github.arhor.simple.expense.tracker.data.model.Expense;
import com.github.arhor.simple.expense.tracker.data.model.ExpenseItem;
import com.github.arhor.simple.expense.tracker.data.model.InternalUser;

@DataJdbcTest
@DirtiesContext
@Tag("integration")
@Testcontainers(disabledWithoutDocker = true)
@ContextConfiguration(classes = DatabaseConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
abstract class RepositoryTestBase {

    @Container
    private final static JdbcDatabaseContainer<?> db = new PostgreSQLContainer<>("postgres:12");

    @DynamicPropertySource
    static void registerDynamicProperties(final DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", db::getJdbcUrl);
        registry.add("spring.datasource.username", db::getUsername);
        registry.add("spring.datasource.password", db::getPassword);
    }

    @Autowired
    protected ExpenseRepository expenseRepository;

    @Autowired
    protected ExpenseItemRepository expenseItemRepository;

    @Autowired
    protected InternalUserRepository userRepository;

    @Autowired
    protected NotificationRepository notificationRepository;

    protected InternalUser createTestUser() {
        return createTestUser(0);
    }

    protected InternalUser createTestUser(final Number number) {
        return InternalUser.builder()
            .username("test-user-username-" + number)
            .password("test-user-password-" + number)
            .currency("USD")
            .externalId(UUID.randomUUID().toString())
            .externalProvider("test")
            .build();
    }

    protected Expense createTestExpense(final Long userId) {
        return createTestExpense(userId, 0);
    }

    protected Expense createTestExpense(final Long userId, final Number number) {
        return Expense.builder()
            .userId(userId)
            .name("test-name-" + number)
            .icon("test-icon-" + number)
            .color("success")
            .build();
    }

    protected InternalUser createPersistedTestUser() {
        return createPersistedTestUser(0);
    }

    protected InternalUser createPersistedTestUser(final Number number) {
        return userRepository.save(createTestUser(number));
    }

    protected Expense createPersistedTestExpense(final Long userId) {
        return createPersistedTestExpense(userId, 0);
    }

    protected Expense createPersistedTestExpense(final Long userId, final Number number) {
        return expenseRepository.save(createTestExpense(userId, number));
    }

    protected List<ExpenseItem> createPersistedTestExpenseItems(
        final int number,
        final Long expenseId,
        final String currency,
        final BigDecimal amount,
        final LocalDate date
    ) {
        val expenseItemsToCreate = Stream.generate(() -> {
                return ExpenseItem.builder()
                    .expenseId(expenseId)
                    .currency(currency)
                    .amount(amount)
                    .date(date)
                    .build();
            })
            .limit(number)
            .toList();

        val result = new ArrayList<ExpenseItem>(number);

        for (val expenseItem : expenseItemRepository.saveAll(expenseItemsToCreate)) {
            result.add(expenseItem);
        }
        return result;
    }
}
