package com.github.arhor.simple.expense.tracker.data.repository;

import lombok.val;

import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import com.github.arhor.simple.expense.tracker.data.model.Expense;

import static org.assertj.core.api.Assertions.assertThat;

class ExpenseRepositoryTest extends RepositoryTestBase {

    @Test
    void should_pass_findAllByUserId() {
        // given
        val userId = createPersistedTestUser().id();

        val expectedExpenses = createExpensesStream(userId)
            .map(expenseRepository::save)
            .toList();

        // when
        val result = expenseRepository.findAllByUserId(userId);

        // then
        assertThat(result)
            .containsExactlyInAnyOrderElementsOf(expectedExpenses);
    }

    private Stream<Expense> createExpensesStream(final Long userId) {
        return IntStream.range(0, 3).mapToObj(number ->
            Expense.builder()
                .userId(userId)
                .name("test-name-" + number)
                .icon("test-icon-" + number)
                .color("success")
                .build()
        );
    }
}
