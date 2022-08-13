package com.github.arhor.simple.expense.tracker.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.experimental.ExtensionMethod;
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
import com.github.arhor.simple.expense.tracker.util.JavaLangExt;
import com.github.arhor.simple.expense.tracker.util.TemporalRange;

@Service
@ExtensionMethod(JavaLangExt.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ExpenseItemServiceImpl implements ExpenseItemService {

    private final ExpenseItemRepository expenseItemRepository;
    private final ExpenseItemMapper expenseItemMapper;

    @Override
    @Transactional(readOnly = true)
    public List<ExpenseItemDTO> getExpenseItems(final Long expenseId, final TemporalRange<LocalDate> dateRange) {
        val startDate = dateRange.start();
        val endDate = dateRange.end();

        return expenseItemRepository.findAllByExpenseIdAndDateRange(expenseId, startDate, endDate).use(stream -> {
            return stream
                .map(expenseItemMapper::mapToDTO)
                .toList();
        });
    }

    @Override
    @Transactional
    public ExpenseItemDTO createExpenseItem(final Long expenseId, final ExpenseItemDTO dto) {
        val entity = expenseItemMapper.mapToEntity(dto, expenseId);
        val result = expenseItemRepository.save(entity);

        return expenseItemMapper.mapToDTO(result);
    }
}
