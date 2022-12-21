package com.github.arhor.simple.expense.tracker.service.mapping

import com.github.arhor.simple.expense.tracker.data.model.ExpenseItem
import com.github.arhor.simple.expense.tracker.model.Currency
import com.github.arhor.simple.expense.tracker.model.ExpenseItemRequestDTO
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.LocalDate
import java.util.function.Consumer

internal class ExpenseItemMapperImplTest : MapperTestBase() {

    @Autowired
    private lateinit var expenseItemMapper: ExpenseItemMapper

    @Test
    fun `should correctly map expense-item entity to dto`() {
        // given
        val expenseItem = ExpenseItem(
            id = 1L,
            date = LocalDate.now(),
            amount = 10.0.toBigDecimal(),
            currency = "USD",
            comment = "test expense item comment",
            expenseId = 10L,
        )

        // when
        val result = expenseItemMapper.mapToDTO(expenseItem)

        // then
        assertThat(result)
            .isNotNull
            .satisfies(
                Consumer {
                    assertThat(it.id)
                        .describedAs("id")
                        .isEqualTo(expenseItem.id)
                },
                {
                    assertThat(it.date)
                        .describedAs("date")
                        .isEqualTo(expenseItem.date)
                },
                {
                    assertThat(it.amount.toBigDecimal())
                        .describedAs("amount")
                        .isEqualTo(expenseItem.amount)
                },
                {
                    assertThat(it.currency.toString())
                        .describedAs("currency")
                        .isEqualTo(expenseItem.currency)
                },
                {
                    assertThat(it.comment)
                        .describedAs("comment")
                        .isEqualTo(expenseItem.comment)
                }
            )
    }

    @Test
    fun `should correctly map expense-item dto to entity`() {
        // given
        val expenseId = 10L
        val expenseItemDTO = ExpenseItemRequestDTO(
            /* id       = */ 1L,
            /* date     = */ LocalDate.now(),
            /* amount   = */ 10.0,
            /* currency = */ Currency.USD,
            /* comment  = */ "test expense item comment",
        )

        // when
        val result = expenseItemMapper.mapToEntity(expenseItemDTO, expenseId)

        // then
        assertThat(result)
            .isNotNull
            .satisfies(
                Consumer {
                    assertThat(it.id)
                        .describedAs("id")
                        .isNull()
                },
                {
                    assertThat(it.date)
                        .describedAs("date")
                        .isEqualTo(expenseItemDTO.date)
                },
                {
                    assertThat(it.amount.toDouble())
                        .describedAs("amount")
                        .isEqualTo(expenseItemDTO.amount)
                },
                {
                    assertThat(Currency.valueOf(it.currency))
                        .describedAs("currency")
                        .isEqualTo(expenseItemDTO.currency)
                },
                {
                    assertThat(it.comment)
                        .describedAs("comment")
                        .isEqualTo(expenseItemDTO.comment)
                },
                {
                    assertThat(it.expenseId)
                        .describedAs("expenseId")
                        .isEqualTo(expenseId)
                }
            )
    }
}
