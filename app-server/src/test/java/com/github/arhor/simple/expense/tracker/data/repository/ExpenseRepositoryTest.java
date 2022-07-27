package com.github.arhor.simple.expense.tracker.data.repository;

import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.github.arhor.simple.expense.tracker.data.model.Expense;

import static com.github.arhor.simple.expense.tracker.data.repository.TestUtils.createPersistedTestUser;
import static org.assertj.core.api.Assertions.assertThat;

class ExpenseRepositoryTest extends RepositoryTestBase {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void should_pass_findAllByUserId() {
        // given
        var userId = createPersistedTestUser(userRepository).getId();

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
