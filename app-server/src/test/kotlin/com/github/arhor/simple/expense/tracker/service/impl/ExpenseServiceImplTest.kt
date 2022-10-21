package com.github.arhor.simple.expense.tracker.service.impl

import com.github.arhor.simple.expense.tracker.data.model.Expense
import com.github.arhor.simple.expense.tracker.data.model.InternalUser
import com.github.arhor.simple.expense.tracker.data.model.projection.AggregatedExpenseItemProjection
import com.github.arhor.simple.expense.tracker.data.repository.ExpenseItemRepository
import com.github.arhor.simple.expense.tracker.data.repository.ExpenseRepository
import com.github.arhor.simple.expense.tracker.data.repository.InternalUserRepository
import com.github.arhor.simple.expense.tracker.exception.EntityNotFoundException
import com.github.arhor.simple.expense.tracker.model.Currency
import com.github.arhor.simple.expense.tracker.model.ExpenseRequestDTO
import com.github.arhor.simple.expense.tracker.model.ExpenseResponseDTO
import com.github.arhor.simple.expense.tracker.service.mapping.ExpenseMapper
import com.github.arhor.simple.expense.tracker.service.money.MoneyConverter
import com.github.arhor.simple.expense.tracker.util.TemporalRange
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowable
import org.assertj.core.api.Assertions.from
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.time.LocalDate
import java.util.Optional

@ExtendWith(MockKExtension::class)
internal class ExpenseServiceImplTest {

    @MockK
    private lateinit var expenseMapper: ExpenseMapper

    @MockK
    private lateinit var expenseRepository: ExpenseRepository

    @MockK
    private lateinit var expenseItemRepository: ExpenseItemRepository

    @MockK
    private lateinit var userRepository: InternalUserRepository

    @MockK
    private lateinit var converter: MoneyConverter

    @InjectMockKs
    private lateinit var expenseService: ExpenseServiceImpl

    @Test
    fun `should return single expense with zero total when there are not associated expense items`() {
        // given
        val expectedId = 1L
        val expectedUserId = 2L
        val expectedName = "test-expense"
        val expectedTotal = 0.0

        every { expenseRepository.findAllByUserId(any()) } answers {
            listOf(Expense(id = expectedId, userId = expectedUserId, name = expectedName))
        }
        every { userRepository.findById(any()) } answers {
            Optional.of(InternalUser(currency = Currency.USD.name))
        }
        every { expenseItemRepository.findAllAggregatedByExpenseIdsAndDateRange(any(), any(), any()) } answers {
            emptyList()
        }
        every { expenseMapper.mapToDTO(entity = any(), total = any()) } answers {
            arg<Expense>(0).let {
                ExpenseResponseDTO(
                    it.id,
                    it.name,
                    it.icon,
                    it.color,
                    arg<Double>(1)
                )
            }
        }

        // when
        val result = expenseService.getUserExpenses(expectedUserId, TemporalRange(start = TODAY, end = TOMORROW))

        // then
        verify { expenseRepository.findAllByUserId(expectedUserId) }
        verify { expenseItemRepository.findAllAggregatedByExpenseIdsAndDateRange(listOf(expectedId), TODAY, TOMORROW) }
        verify { expenseMapper.mapToDTO(any(), expectedTotal) }
        verify { userRepository.findById(expectedUserId) }

        confirmVerified(expenseMapper, expenseRepository, expenseItemRepository, userRepository, converter)

        assertThat(result)
            .singleElement()
            .returns(expectedId, from { it.id })
            .returns(expectedName, from { it.name })
            .returns(expectedTotal, from { it.total })
    }

    @Test
    fun `should return single expense with expected total when there is exactly one associated expense item`() {
        // given
        val expectedId = 1L
        val expectedUserId = 2L
        val expectedName = "test-expense"
        val expectedTotal = 10.0

        every { expenseRepository.findAllByUserId(any()) } answers {
            listOf(
                Expense(
                    id = expectedId,
                    userId = expectedUserId,
                    name = expectedName
                )
            )
        }
        every { userRepository.findById(any()) } answers {
            Optional.of(
                InternalUser(
                    currency = Currency.USD.name
                )
            )
        }
        every { expenseItemRepository.findAllAggregatedByExpenseIdsAndDateRange(any(), any(), any()) } answers {
            listOf(
                AggregatedExpenseItemProjection(
                    expenseId = expectedId,
                    date = TODAY, currency = Currency.USD.name,
                    totalAmount = expectedTotal.toBigDecimal()
                )
            )
        }
        every { converter.convert(amount = any(), currency = any(), conversionDate = any()) } answers {
            firstArg()
        }
        every { expenseMapper.mapToDTO(entity = any(), total = any()) } answers {
            arg<Expense>(0).let {
                ExpenseResponseDTO(
                    it.id,
                    it.name,
                    it.icon,
                    it.color,
                    arg<Double>(1)
                )
            }
        }

        // when
        val result = expenseService.getUserExpenses(expectedUserId, TemporalRange(start = TODAY, end = TOMORROW))

        // then
        verify { expenseRepository.findAllByUserId(expectedUserId) }
        verify { expenseItemRepository.findAllAggregatedByExpenseIdsAndDateRange(listOf(expectedId), TODAY, TOMORROW) }
        verify { converter.convert(any(), any(), any()) }
        verify { expenseMapper.mapToDTO(any(), expectedTotal) }
        verify { userRepository.findById(expectedUserId) }

        confirmVerified(expenseMapper, expenseRepository, expenseItemRepository, userRepository, converter)

        assertThat(result)
            .singleElement()
            .returns(expectedId, from { it.id })
            .returns(expectedName, from { it.name })
            .returns(expectedTotal, from { it.total })
    }

    @Test
    fun `should return empty list when the user has no expenses`() {
        // given
        val expectedUserId = 1L
        val expectedDateRange = TemporalRange(start = TODAY, end = TOMORROW)

        every { expenseRepository.findAllByUserId(any()) } returns emptyList()

        // when
        val result = expenseService.getUserExpenses(expectedUserId, expectedDateRange)

        // then
        verify { expenseRepository.findAllByUserId(expectedUserId) }

        confirmVerified(expenseMapper, expenseRepository, expenseItemRepository, userRepository, converter)

        assertThat(result)
            .isEmpty()
    }

    @Test
    fun `should throw EntityNotFoundException getting a non existing expense by id`() {
        // given
        val expectedExpenseId = 1L
        val expectedDateRange = TemporalRange(start = TODAY, end = TOMORROW)

        val expectedExceptionParams = arrayOf(
            "Expense",
            "id=${expectedExpenseId}"
        )

        every { expenseRepository.findById(any()) } returns Optional.empty()

        // when
        val result = catchThrowable { expenseService.getExpenseById(expectedExpenseId, expectedDateRange) }

        // then
        verify { expenseRepository.findById(expectedExpenseId) }

        confirmVerified(expenseMapper, expenseRepository, expenseItemRepository, userRepository, converter)

        assertThat(result)
            .isInstanceOf(EntityNotFoundException::class.java)
            .extracting { it as EntityNotFoundException }
            .returns(expectedExceptionParams, from { it.params })
    }

    @Test
    fun `should throw EntityNotFoundException creating expense for a non existing user by id`() {
        // given
        val expectedUserId = 1L
        val expectedExceptionParams = arrayOf(
            "InternalUser",
            "id=${expectedUserId}",
        )

        every { userRepository.existsById(any()) } returns false

        // when
        val result = catchThrowable { expenseService.createUserExpense(expectedUserId, ExpenseRequestDTO()) }

        // then
        verify { userRepository.existsById(expectedUserId) }

        confirmVerified(expenseMapper, expenseRepository, expenseItemRepository, userRepository, converter)

        assertThat(result)
            .isInstanceOf(EntityNotFoundException::class.java)
            .extracting { it as EntityNotFoundException }
            .returns(expectedExceptionParams, from { it.params })
    }

    companion object {
        private val TODAY = LocalDate.now()
        private val TOMORROW = TODAY.plusDays(1)
    }
}
