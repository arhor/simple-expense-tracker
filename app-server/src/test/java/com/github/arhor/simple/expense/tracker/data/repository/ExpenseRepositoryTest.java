package com.github.arhor.simple.expense.tracker.data.repository;

import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.junit.jupiter.Container;

import com.github.arhor.simple.expense.tracker.data.model.Expense;

import static org.assertj.core.api.Assertions.assertThat;

class ExpenseRepositoryTest extends RepositoryTestBase {

    @Container
    private static final JdbcDatabaseContainer<?> db = createDatabaseContainer();

    @DynamicPropertySource
    static void registerDynamicProperties(final DynamicPropertyRegistry registry) {
        registerDatasource(registry, db);
    }

    @Autowired
    private ExpenseRepository expenseRepository;

    @Test
    void should_pass_findAllByUserId() {
        // given
        var userId = createPersistedTestUser().getId();

        var expectedExpenses = createExpensesStream(userId)
            .map(expenseRepository::save)
            .toList();

        // when
        var result = expenseRepository.findAllByUserId(userId);

        // then
        assertThat(result)
            .containsExactlyInAnyOrderElementsOf(expectedExpenses);
    }

    private Stream<Expense> createExpensesStream(final Long userId) {
        return IntStream.range(0, 3).mapToObj(number ->
            Expense.builder()
                .userId(userId)
                .name("test-expense-name-" + number)
                .icon("test-expense-icon-" + number)
                .color("success")
                .build()
        );
    }
}
