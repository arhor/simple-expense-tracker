package com.github.arhor.simple.expense.tracker.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.val;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.arhor.simple.expense.tracker.data.repository.ExpenseItemRepository;
import com.github.arhor.simple.expense.tracker.model.ExpenseItemDTO;
import com.github.arhor.simple.expense.tracker.service.ExpenseItemService;
import com.github.arhor.simple.expense.tracker.service.mapping.ExpenseItemMapper;
import com.github.arhor.simple.expense.tracker.util.TemporalRange;

import static com.github.arhor.simple.expense.tracker.util.StreamUtils.useStream;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ExpenseItemServiceImpl implements ExpenseItemService {

    private final ExpenseItemRepository expenseItemRepository;
    private final ExpenseItemMapper expenseItemMapper;

    @Override
    @Transactional(readOnly = true)
    public List<ExpenseItemDTO> getExpenseItems(final Long expenseId, final TemporalRange<LocalDate> dateRange) {
        return useStream(
            expenseItemRepository.findAllByExpenseIdAndDateRange(
                expenseId,
                dateRange.start(),
                dateRange.end()
            ),
            stream -> {
                return stream
                    .map(expenseItemMapper::mapToDTO)
                    .toList();
            }
        );
    }

    @Override
    @Transactional
    public ExpenseItemDTO createExpenseItem(final Long expenseId, final ExpenseItemDTO dto) {
        val entity = expenseItemMapper.mapToEntity(dto, expenseId);
        val result = expenseItemRepository.save(entity);

        return expenseItemMapper.mapToDTO(result);
    }
}
