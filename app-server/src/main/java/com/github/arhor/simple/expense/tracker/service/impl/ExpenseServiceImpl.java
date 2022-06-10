package com.github.arhor.simple.expense.tracker.service.impl;

import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.arhor.simple.expense.tracker.data.model.ExpenseItem;
import com.github.arhor.simple.expense.tracker.data.repository.ExpenseItemRepository;
import com.github.arhor.simple.expense.tracker.data.repository.ExpenseRepository;
import com.github.arhor.simple.expense.tracker.data.repository.UserRepository;
import com.github.arhor.simple.expense.tracker.exception.EntityNotFoundException;
import com.github.arhor.simple.expense.tracker.model.ExpenseItemDTO;
import com.github.arhor.simple.expense.tracker.model.ExpenseRequestDTO;
import com.github.arhor.simple.expense.tracker.model.ExpenseResponseDTO;
import com.github.arhor.simple.expense.tracker.service.ExpenseService;
import com.github.arhor.simple.expense.tracker.service.TimeService.TemporalRange;
import com.github.arhor.simple.expense.tracker.service.mapping.ExpenseConverter;
import com.github.arhor.simple.expense.tracker.service.mapping.ExpenseItemConverter;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ExpenseServiceImpl implements ExpenseService {

    private final UserRepository userRepository;
    private final ExpenseRepository expenseRepository;
    private final ExpenseItemRepository expenseItemRepository;
    private final ExpenseConverter expenseConverter;
    private final ExpenseItemConverter expenseItemConverter;

    @Override
    public List<ExpenseResponseDTO> getUserExpenses(
        final Long userId,
        final TemporalRange<LocalDateTime> dateTimeRange
    ) {
        var expenses = expenseRepository.findByUserId(userId);

        if (expenses.isEmpty()) {
            return Collections.emptyList();
        }

        var result = new ArrayList<ExpenseResponseDTO>(expenses.size());

        for (var expense : expenses) {
            var responseDTO = expenseConverter.mapToDTO(expense);

            initializeExpenseTotal(responseDTO, dateTimeRange);

            result.add(responseDTO);
        }
        return result;
    }

    @Override
    public ExpenseResponseDTO getUserExpenseById(
        final Long userId,
        final Long expenseId,
        final TemporalRange<LocalDateTime> dateTimeRange
    ) {
        var responseDTO = expenseRepository.findByUserIdAndExpenseId(userId, expenseId)
            .map(expenseConverter::mapToDTO)
            .orElseThrow(() -> new EntityNotFoundException("Expense", "userId=" + userId + ", expenseId=" + expenseId));

        initializeExpenseTotal(responseDTO, dateTimeRange);

        return responseDTO;
    }

    @Override
    public ExpenseResponseDTO createUserExpense(final Long userId, final ExpenseRequestDTO requestDTO) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("InternalUser", "id=" + userId);
        }

        var expense = expenseConverter.mapToEntity(requestDTO);
        expense.setUserId(userId);
        var result = expenseRepository.save(expense);

        return expenseConverter.mapToDTO(result);
    }

    @Override
    public ExpenseItemDTO createExpenseItem(final Long expenseId, final ExpenseItemDTO dto) {
        return null;
    }

    private void initializeExpenseTotal(
        final ExpenseResponseDTO expense,
        final TemporalRange<LocalDateTime> dateTimeRange
    ) {
        var expenseItems = expenseItemRepository.findByExpenseIdAndDateRange(
            expense.getId(),
            dateTimeRange.start(),
            dateTimeRange.end()
        );

        var total = expenseItems.stream()
            .filter(Objects::nonNull)
            .map(ExpenseItem::getAmount)
            .filter(Objects::nonNull)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        expense.setTotal(total);
    }
}
