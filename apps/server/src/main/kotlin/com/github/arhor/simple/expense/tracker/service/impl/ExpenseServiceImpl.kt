package com.github.arhor.simple.expense.tracker.service.impl

import com.github.arhor.simple.expense.tracker.data.model.InternalUser
import com.github.arhor.simple.expense.tracker.data.model.projection.AggregatedExpenseItemProjection
import com.github.arhor.simple.expense.tracker.data.repository.ExpenseItemRepository
import com.github.arhor.simple.expense.tracker.data.repository.ExpenseRepository
import com.github.arhor.simple.expense.tracker.data.repository.InternalUserRepository
import com.github.arhor.simple.expense.tracker.service.exception.EntityNotFoundException
import com.github.arhor.simple.expense.tracker.model.ExpenseRequestDTO
import com.github.arhor.simple.expense.tracker.model.ExpenseResponseDTO
import com.github.arhor.simple.expense.tracker.service.ExpenseService
import com.github.arhor.simple.expense.tracker.service.mapping.ExpenseMapper
import com.github.arhor.simple.expense.tracker.service.money.MoneyConverter
import com.github.arhor.simple.expense.tracker.service.util.TemporalRange
import org.javamoney.moneta.Money
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.time.LocalDate
import javax.money.CurrencyUnit
import javax.money.Monetary

@Service
class ExpenseServiceImpl(
    private val expenseMapper: ExpenseMapper,
    private val expenseRepository: ExpenseRepository,
    private val expenseItemRepository: ExpenseItemRepository,
    private val userRepository: InternalUserRepository,
    private val converter: MoneyConverter,
) : ExpenseService {

    override fun getUserExpenses(userId: Long, dateRange: TemporalRange<LocalDate>): List<ExpenseResponseDTO> {
        val expenses = expenseRepository.findAllByUserId(userId)
        val expenseTotalsById = getExpenseItemsTotal(userId, dateRange, *expenses.map { it.id }.toTypedArray())

        return expenses.map {
            expenseMapper.mapToDTO(
                it,
                expenseTotalsById.getOrDefault(it.id, 0.0)
            )
        }
    }

    override fun getExpenseById(expenseId: Long, dateRange: TemporalRange<LocalDate>): ExpenseResponseDTO {
        val expense = expenseRepository.findByIdOrNull(expenseId)
            ?: throw EntityNotFoundException("Expense", "id=$expenseId")
        val expenseTotalsById = getExpenseItemsTotal(expense.userId, dateRange, expense.id)

        return expense.let {
            expenseMapper.mapToDTO(
                it,
                expenseTotalsById.getOrDefault(it.id, 0.0)
            )
        }
    }

    override fun createUserExpense(userId: Long, requestDTO: ExpenseRequestDTO): ExpenseResponseDTO {
        if (!userRepository.existsById(userId)) {
            throw EntityNotFoundException("InternalUser", "id=$userId")
        }

        val expense = expenseMapper.mapToEntity(requestDTO, userId)
        val result = expenseRepository.save(expense)

        return expenseMapper.mapToDTO(result, 0.0)
    }

    private fun getUserCurrency(userId: Long): CurrencyUnit {
        return userRepository.findByIdOrNull(userId)
            ?.let(InternalUser::currency)
            ?.let(Monetary::getCurrency)
            ?: throw EntityNotFoundException("User", "id = $userId")
    }

    private fun getExpenseItemsTotal(
        userId: Long,
        dateRange: TemporalRange<LocalDate>,
        vararg expenseIds: Long?,
    ): Map<Long, Double> {
        if (expenseIds.isEmpty()) {
            return emptyMap()
        }
        val targetCurrency = getUserCurrency(userId)
        val totalByExpense = HashMap<Long, TotalCalculationContext>()

        val aggregatedExpenseItems = expenseItemRepository.findAllAggregatedByExpenseIdsAndDateRange(
            expenseIds.filterNotNull(),
            dateRange.start,
            dateRange.end,
        )

        for (expenseItem in aggregatedExpenseItems) {
            val expenseTotalCalculator = totalByExpense.computeIfAbsent(expenseItem.expenseId) {
                TotalCalculationContext(targetCurrency)
            }
            expenseTotalCalculator.add(expenseItem)
        }

        val result = HashMap<Long, Double>()
        for ((expenseId, context) in totalByExpense) {
            result[expenseId] = context.total.number.doubleValueExact()
        }
        return result
    }

    /**
     * Money total calculation context. Convenient to use with streams since it allows mutations as a side effect.
     */
    private inner class TotalCalculationContext(currency: CurrencyUnit) {

        var total: Money = Money.zero(currency)

        fun add(expenseItem: AggregatedExpenseItemProjection) {
            val sourceCurrency = expenseItem.currency
            val targetCurrency = total.currency

            val amount = Money.of(expenseItem.totalAmount, sourceCurrency)
            val result = converter.convert(amount, targetCurrency, expenseItem.date)

            total = total.add(result)
        }
    }
}
