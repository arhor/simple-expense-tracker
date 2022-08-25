package com.github.arhor.simple.expense.tracker.service.impl

import com.github.arhor.simple.expense.tracker.data.repository.ExpenseItemRepository
import com.github.arhor.simple.expense.tracker.model.ExpenseItemDTO
import com.github.arhor.simple.expense.tracker.service.ExpenseItemService
import com.github.arhor.simple.expense.tracker.service.mapping.ExpenseItemMapper
import com.github.arhor.simple.expense.tracker.util.TemporalRange
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class ExpenseItemServiceImpl(
    private val expenseItemRepository: ExpenseItemRepository,
    private val expenseItemMapper: ExpenseItemMapper,
) : ExpenseItemService {

    @Transactional(readOnly = true)
    override fun getExpenseItems(expenseId: Long, dateRange: TemporalRange<LocalDate>): List<ExpenseItemDTO> {
        return expenseItemRepository.findAllByExpenseIdAndDateRange(
            expenseId,
            dateRange.start,
            dateRange.end,
        ).use {
            it.map(expenseItemMapper::mapToDTO).toList()
        }
    }

    @Transactional
    override fun createExpenseItem(expenseId: Long, dto: ExpenseItemDTO): ExpenseItemDTO {
        val entity = expenseItemMapper.mapToEntity(dto, expenseId)
        val result = expenseItemRepository.save(entity)

        return expenseItemMapper.mapToDTO(result)
    }
}
