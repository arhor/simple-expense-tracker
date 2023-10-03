package com.github.arhor.simple.expense.tracker.data.repository

import com.github.arhor.simple.expense.tracker.data.model.Expense
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.stream.IntStream

internal class ExpenseRepositoryTest : RepositoryTestBase() {

    @Test
    fun `should pass findAllByUserId`() {
        // given
        val userId = createPersistedTestUser().id!!

        val expectedExpenses = createExpensesStream(userId)
            .map(expenseRepository::save)
            .toList()

        // when
        val result = expenseRepository.findAllByUserId(userId)

        // then
        assertThat(result)
            .containsExactlyInAnyOrderElementsOf(expectedExpenses)
    }

    private fun createExpensesStream(userId: Long) = IntStream.range(0, 3).mapToObj {
        Expense(
            userId = userId,
            name = "test-name-$it",
            icon = "test-icon-$it",
            color = "success",
        )
    }
}
