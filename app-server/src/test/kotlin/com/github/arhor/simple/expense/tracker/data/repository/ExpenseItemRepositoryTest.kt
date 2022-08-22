package com.github.arhor.simple.expense.tracker.data.repository


import com.github.arhor.simple.expense.tracker.data.model.ExpenseItem
import com.github.arhor.simple.expense.tracker.data.model.projection.AggregatedExpenseItemProjection
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDate
import java.util.function.Consumer

internal class ExpenseItemRepositoryTest : RepositoryTestBase() {

    @Test
    fun `should return aggregated expense items grouped by date and currency`() {
        // given
        val userId = createPersistedTestUser().id()
        val expense = createPersistedTestExpense(userId)
        val expenseDate = LocalDate.of(2022, 7, 1)
        val usdCurrency = "USD"
        val jpyCurrency = "JPY"

        val usdExpenseItems =
            createPersistedTestExpenseItems(3, expense.id, usdCurrency, BigDecimal.TEN, expenseDate)
        val jpyExpenseItems =
            createPersistedTestExpenseItems(2, expense.id, jpyCurrency, BigDecimal.TEN, expenseDate)

        val expectedUsdAggregatedExpenseItem = AggregatedExpenseItemProjection(
            expense.id!!,
            expenseDate,
            usdCurrency,
            totalFrom(usdExpenseItems)
        )
        val expectedJpyAggregatedExpenseItem = AggregatedExpenseItemProjection(
            expense.id!!,
            expenseDate,
            jpyCurrency,
            totalFrom(jpyExpenseItems)
        )

        // when
        val result =
            expenseItemRepository.findAllAggregatedByExpenseIdsAndDateRange(
                listOf(expense.id!!),
                expenseDate,
                expenseDate
            )

        // then
        assertThat(result)
            .hasSize(2)
            .satisfies(
                Consumer { items ->
                    assertThatAggregatedExpenseItemByCurrency(items, usdCurrency)
                        .isNotNull
                        .satisfies(
                            Consumer {
                                assertThat(it?.expenseId)
                                    .describedAs("expense id")
                                    .isEqualTo(expectedUsdAggregatedExpenseItem.expenseId)
                            },
                            {
                                assertThat(it?.date)
                                    .describedAs("expense date")
                                    .isEqualTo(expectedUsdAggregatedExpenseItem.date)
                            },
                            {
                                assertThat(it?.currency)
                                    .describedAs("expense currency")
                                    .isEqualTo(expectedUsdAggregatedExpenseItem.currency)
                            },
                            {
                                assertThat(it?.totalAmount)
                                    .describedAs("expense total amount")
                                    .isEqualByComparingTo(expectedUsdAggregatedExpenseItem.totalAmount)
                            }
                        )
                },
                { items ->
                    assertThatAggregatedExpenseItemByCurrency(items, jpyCurrency)
                        .isNotNull
                        .satisfies(
                            Consumer {
                                assertThat(it?.expenseId)
                                    .describedAs("expense id")
                                    .isEqualTo(expectedJpyAggregatedExpenseItem.expenseId)
                            },
                            {
                                assertThat(it?.date)
                                    .describedAs("expense date")
                                    .isEqualTo(expectedJpyAggregatedExpenseItem.date)
                            },
                            {
                                assertThat(it?.currency)
                                    .describedAs("expense currency")
                                    .isEqualTo(expectedJpyAggregatedExpenseItem.currency)
                            },
                            {
                                assertThat(it?.totalAmount)
                                    .describedAs("expense total amount")
                                    .isEqualByComparingTo(expectedJpyAggregatedExpenseItem.totalAmount)
                            }
                        )
                }
            )
    }

    private fun totalFrom(expenseItems: List<ExpenseItem>): BigDecimal {
        return expenseItems.map(ExpenseItem::amount).fold(BigDecimal.ZERO, BigDecimal::add)
    }

    private fun assertThatAggregatedExpenseItemByCurrency(
        data: Collection<AggregatedExpenseItemProjection>,
        currency: String,
    ) = data.find { currency == it.currency }.let {
        assertThat(it).describedAs("aggregated expense item for the %s currency", currency)
    }
}
