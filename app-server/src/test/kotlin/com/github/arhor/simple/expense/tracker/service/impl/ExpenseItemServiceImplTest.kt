package com.github.arhor.simple.expense.tracker.service.impl

import com.github.arhor.simple.expense.tracker.data.model.ExpenseItem
import com.github.arhor.simple.expense.tracker.data.repository.ExpenseItemRepository
import com.github.arhor.simple.expense.tracker.model.Currency
import com.github.arhor.simple.expense.tracker.model.ExpenseItemDTO
import com.github.arhor.simple.expense.tracker.service.mapping.ExpenseItemMapper
import com.github.arhor.simple.expense.tracker.util.TemporalRange
import io.mockk.called
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.from
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.math.BigDecimal
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
        val expectedAmount = BigDecimal.TEN
        val expectedCurrency = Currency.USD.name
        val expectedComment = "test-comment"

        val expenseItemEntity = slot<ExpenseItem>()

        every { expenseItemRepository.findAllByExpenseIdAndDateRange(any(), any(), any()) } answers {
            Stream.of(
                ExpenseItem(
                    expenseId = expectedExpenseId,
                    date = expectedDate,
                    amount = expectedAmount,
                    currency = expectedCurrency,
                    comment = expectedComment,
                )
            )
        }
        every { expenseItemMapper.mapToDTO(capture(expenseItemEntity)) } answers {
            expenseItemEntity.captured.let {
                ExpenseItemDTO(
                    it.date,
                    it.amount,
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
            .returns(expectedComment, from { it.comment.get() })
    }

    @Test
    fun `should correctly save new expense item entity using provided DTO`() {
        // given
        val expectedExpenseItem = ExpenseItemDTO().apply {
            date = LocalDate.of(2022, 9, 5)
            amount = BigDecimal.TEN
            currency = Currency.USD
            setComment("test comment")
        }

        val expenseId = slot<Long>()
        val expenseItemDto = slot<ExpenseItemDTO>()
        val expenseItemEntity = slot<ExpenseItem>()

        every { expenseItemMapper.mapToEntity(capture(expenseItemDto), capture(expenseId)) } answers {
            expenseItemDto.captured.let {
                ExpenseItem(
                    expenseId = expenseId.captured,
                    date = it.date,
                    amount = it.amount,
                    currency = it.currency.name,
                    comment = it.comment.get(),
                )
            }
        }
        every { expenseItemRepository.save(capture(expenseItemEntity)) } answers {
            expenseItemEntity.captured
        }
        every { expenseItemMapper.mapToDTO(capture(expenseItemEntity)) } answers {
            expenseItemEntity.captured.let {
                ExpenseItemDTO(
                    it.date,
                    it.amount,
                    it.currency.let(Currency::fromValue),
                    it.comment,
                )
            }
        }

        // when
        val createdExpenseItem = expenseItemService.createExpenseItem(
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
