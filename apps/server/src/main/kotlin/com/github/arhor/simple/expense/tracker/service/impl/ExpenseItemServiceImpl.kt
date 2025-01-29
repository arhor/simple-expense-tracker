package com.github.arhor.simple.expense.tracker.service.impl

import com.github.arhor.simple.expense.tracker.data.repository.ExpenseItemRepository
import com.github.arhor.simple.expense.tracker.model.ExpenseItemRequestDTO
import com.github.arhor.simple.expense.tracker.model.ExpenseItemResponseDTO
import com.github.arhor.simple.expense.tracker.service.ExpenseItemService
import com.github.arhor.simple.expense.tracker.service.mapping.ExpenseItemMapper
import com.github.arhor.simple.expense.tracker.service.util.TemporalRange
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class ExpenseItemServiceImpl(
    private val expenseItemRepository: ExpenseItemRepository,
    private val expenseItemMapper: ExpenseItemMapper,
) : ExpenseItemService {

    @Transactional(readOnly = true)
    override fun getExpenseItems(expenseId: Long, dateRange: TemporalRange<LocalDate>): List<ExpenseItemResponseDTO> =
        expenseItemRepository.findAllByExpenseIdAndDateRange(expenseId, dateRange.start, dateRange.end).use { data ->
            data.map(expenseItemMapper::mapToDTO)
                .toList()
        }

    @Transactional
    override fun createExpenseItem(userId: Long, expenseId: Long, dto: ExpenseItemRequestDTO): ExpenseItemResponseDTO {

        // ensure current user owns this expense


        val entity = expenseItemMapper.mapToEntity(dto, expenseId)
        val result = expenseItemRepository.save(entity)

        return expenseItemMapper.mapToDTO(result)
    }
}
