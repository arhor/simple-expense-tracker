package com.github.arhor.simple.expense.tracker.data.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import org.assertj.core.api.OptionalAssert;
import org.junit.jupiter.api.Test;

import com.github.arhor.simple.expense.tracker.data.model.ExpenseItem;
import com.github.arhor.simple.expense.tracker.data.model.projection.AggregatedExpenseItemProjection;

import static org.assertj.core.api.Assertions.assertThat;

class ExpenseItemRepositoryTest extends RepositoryTestBase {

    @Test
    void should_return_aggregated_expense_items_grouped_by_date_and_currency() {
        // given
        var userId = createPersistedTestUser().id();
        var expense = createPersistedTestExpense(userId);
        var expenseDate = LocalDate.of(2022, 7, 1);
        var usdCurrency = "USD";
        var jpyCurrency = "JPY";

        var usdExpenseItems =
            createPersistedTestExpenseItems(3, expense.id(), usdCurrency, BigDecimal.TEN, expenseDate);
        var jpyExpenseItems =
            createPersistedTestExpenseItems(2, expense.id(), jpyCurrency, BigDecimal.TEN, expenseDate);

        var expectedUsdAggregatedExpenseItem = new AggregatedExpenseItemProjection(
            expense.id(),
            expenseDate,
            usdCurrency,
            totalFrom(usdExpenseItems)
        );
        var expectedJpyAggregatedExpenseItem = new AggregatedExpenseItemProjection(
            expense.id(),
            expenseDate,
            jpyCurrency,
            totalFrom(jpyExpenseItems)
        );

        // when
        var result =
            expenseItemRepository.findAllAggregatedByExpenseIdsAndDateRange(
                List.of(expense.id()),
                expenseDate,
                expenseDate
            );

        // then
        assertThat(result)
            .hasSize(2)
            .satisfies(
                items -> {
                    assertThatAggregatedExpenseItemByCurrency(items, usdCurrency)
                        .isNotEmpty()
                        .get()
                        .satisfies(
                            item -> {
                                assertThat(item.expenseId())
                                    .as("expense id")
                                    .isEqualTo(expectedUsdAggregatedExpenseItem.expenseId());
                            },
                            item -> {
                                assertThat(item.date())
                                    .as("expense date")
                                    .isEqualTo(expectedUsdAggregatedExpenseItem.date());
                            },
                            item -> {
                                assertThat(item.currency())
                                    .as("expense currency")
                                    .isEqualTo(expectedUsdAggregatedExpenseItem.currency());
                            },
                            item -> {
                                assertThat(item.totalAmount())
                                    .as("expense total amount")
                                    .isEqualByComparingTo(expectedUsdAggregatedExpenseItem.totalAmount());
                            }
                        );
                },
                items -> {
                    assertThatAggregatedExpenseItemByCurrency(items, jpyCurrency)
                        .isNotEmpty().get()
                        .satisfies(
                            item -> {
                                assertThat(item.expenseId())
                                    .as("expense id")
                                    .isEqualTo(expectedJpyAggregatedExpenseItem.expenseId());
                            },
                            item -> {
                                assertThat(item.date())
                                    .as("expense date")
                                    .isEqualTo(expectedJpyAggregatedExpenseItem.date());
                            },
                            item -> {
                                assertThat(item.currency())
                                    .as("expense currency")
                                    .isEqualTo(expectedJpyAggregatedExpenseItem.currency());
                            },
                            item -> {
                                assertThat(item.totalAmount())
                                    .as("expense total amount")
                                    .isEqualByComparingTo(expectedJpyAggregatedExpenseItem.totalAmount());
                            }
                        );
                }
            );
    }

    private BigDecimal totalFrom(final List<ExpenseItem> expenseItems) {
        return expenseItems.stream().map(ExpenseItem::amount).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private <T extends AggregatedExpenseItemProjection> OptionalAssert<T> assertThatAggregatedExpenseItemByCurrency(
        final Collection<T> data,
        final String currency
    ) {
        var aggregatedExpenseItem =
            data.stream()
                .filter(it -> currency.equals(it.currency()))
                .findFirst();

        return assertThat(aggregatedExpenseItem).as("aggregated expense item for the %s currency", currency);
    }
}
