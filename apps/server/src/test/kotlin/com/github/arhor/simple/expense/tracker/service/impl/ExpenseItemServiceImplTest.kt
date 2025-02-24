package com.github.arhor.simple.expense.tracker.service.impl

import com.github.arhor.simple.expense.tracker.data.model.ExpenseItem
import com.github.arhor.simple.expense.tracker.data.repository.ExpenseItemRepository
import com.github.arhor.simple.expense.tracker.model.Currency
import com.github.arhor.simple.expense.tracker.model.ExpenseItemRequestDTO
import com.github.arhor.simple.expense.tracker.model.ExpenseItemResponseDTO
import com.github.arhor.simple.expense.tracker.service.mapping.ExpenseItemMapper
import com.github.arhor.simple.expense.tracker.service.util.TemporalRange
import io.mockk.called
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.from
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.time.LocalDate
import java.util.stream.Stream

@ExtendWith(MockKExtension::class)
internal class ExpenseItemServiceImplTest {

    @MockK
    private lateinit var expenseItemRepository: ExpenseItemRepository

    @MockK
    private lateinit var expenseItemMapper: ExpenseItemMapper

    @InjectMockKs
    private lateinit var expenseItemService: ExpenseItemServiceImpl

    @Test
    fun `should return an empty list when there are not expense items within provided date range`() {
        // given
        val expectedExpenseId = -1L
        val expectedDateRangeStart = LocalDate.of(2022, 9, 1)
        val expectedDateRangeEnd = LocalDate.of(2022, 9, 7)
        val expectedDateRange = TemporalRange(expectedDateRangeStart, expectedDateRangeEnd)

        every { expenseItemRepository.findAllByExpenseIdAndDateRange(any(), any(), any()) } returns Stream.empty()

        // when
        val result = expenseItemService.getExpenseItems(expectedExpenseId, expectedDateRange)

        // then
        verify(exactly = 1) {
            expenseItemRepository.findAllByExpenseIdAndDateRange(
                expenseId = expectedExpenseId,
                startDate = expectedDateRangeStart,
                endDate = expectedDateRangeEnd
            )
        }
        verify { expenseItemMapper wasNot called }

        assertThat(result)
            .isEmpty()
    }

    @Test
    fun `should return a list containing exactly one expected expense item`() {
        // given
        val expectedExpenseId = -1L
        val expectedDateRangeStart = LocalDate.of(2022, 9, 1)
        val expectedDateRangeEnd = LocalDate.of(2022, 9, 7)
        val expectedDateRange = TemporalRange(expectedDateRangeStart, expectedDateRangeEnd)
        val expectedDate = LocalDate.of(2022, 9, 3)
        val expectedAmount = 10.0
        val expectedCurrency = Currency.USD.name
        val expectedComment = "test-comment"

        every { expenseItemRepository.findAllByExpenseIdAndDateRange(any(), any(), any()) } answers {
            Stream.of(
                ExpenseItem(
                    expenseId = expectedExpenseId,
                    date = expectedDate,
                    amount = expectedAmount.toBigDecimal(),
                    currency = expectedCurrency,
                    comment = expectedComment,
                )
            )
        }
        every { expenseItemMapper.mapToDTO(entity = any()) } answers {
            arg<ExpenseItem>(0).let {
                ExpenseItemResponseDTO(
                    it.id,
                    it.date,
                    it.amount.toDouble(),
                    it.currency.let(Currency::fromValue),
                    it.comment,
                )
            }
        }

        // when
        val result = expenseItemService.getExpenseItems(expectedExpenseId, expectedDateRange)

        // then
        verify(exactly = 1) {
            expenseItemRepository.findAllByExpenseIdAndDateRange(
                expenseId = expectedExpenseId,
                startDate = expectedDateRangeStart,
                endDate = expectedDateRangeEnd
            )
        }
        verify(exactly = 1) { expenseItemMapper.mapToDTO(entity = any()) }

        assertThat(result)
            .singleElement()
            .returns(expectedDate, from { it.date })
            .returns(expectedAmount, from { it.amount })
            .returns(expectedCurrency, from { it.currency.name })
            .returns(expectedComment, from { it.comment })
    }

    @Test
    fun `should correctly save new expense item entity using provided DTO`() {
        // given
        val expectedExpenseItem = ExpenseItemRequestDTO().apply {
            date = LocalDate.of(2022, 9, 5)
            amount = 10.0
            currency = Currency.USD
            comment = "test comment"
        }

        every { expenseItemMapper.mapToEntity(dto = any(), expenseId = any()) } answers {
            arg<ExpenseItemRequestDTO>(0).let {
                ExpenseItem(
                    expenseId = arg(1),
                    date = it.date,
                    amount = it.amount.toBigDecimal(),
                    currency = it.currency.name,
                    comment = it.comment,
                )
            }
        }
        every { expenseItemRepository.save(any()) } answers {
            arg(0)
        }
        every { expenseItemMapper.mapToDTO(any()) } answers {
            arg<ExpenseItem>(0).let {
                ExpenseItemResponseDTO(
                    it.id,
                    it.date,
                    it.amount.toDouble(),
                    it.currency.let(Currency::fromValue),
                    it.comment,
                )
            }
        }

        // when
        val createdExpenseItem = expenseItemService.createExpenseItem(
            userId = -1L,
            expenseId = -1L,
            dto = expectedExpenseItem
        )

        // then
        verify(exactly = 1) { expenseItemMapper.mapToEntity(dto = any(), expenseId = any()) }
        verify(exactly = 1) { expenseItemRepository.save(any()) }
        verify(exactly = 1) { expenseItemMapper.mapToEntity(dto = any(), expenseId = any()) }

        assertThat(createdExpenseItem)
            .returns(expectedExpenseItem.date, from { it.date })
            .returns(expectedExpenseItem.amount, from { it.amount })
            .returns(expectedExpenseItem.currency, from { it.currency })
            .returns(expectedExpenseItem.comment, from { it.comment })
    }
}
