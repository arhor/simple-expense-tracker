package com.github.arhor.simple.expense.tracker.data.repository;

import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import com.github.arhor.simple.expense.tracker.data.model.Expense;
import com.github.arhor.simple.expense.tracker.data.repository.support.AggregatedExpenseExtractor;

import static com.github.arhor.simple.expense.tracker.data.repository.TestUtils.createPersistedTestUser;
import static org.assertj.core.api.Assertions.assertThat;

@Import(AggregatedExpenseExtractor.class)
class ExpenseRepositoryTest extends RepositoryTestBase {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private ExpenseItemRepository expenseItemRepository;

    @Autowired
    private InternalUserRepository userRepository;

    @Test
    void should_pass_findAllByUserId() {
        // given
        var userId = createPersistedTestUser(userRepository).id();

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
                .name("test-name-" + number)
                .icon("test-icon-" + number)
                .color("success")
                .build()
        );
    }
}
