package com.github.arhor.simple.expense.tracker.service.impl;

import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.arhor.simple.expense.tracker.data.repository.ExpenseRepository;
import com.github.arhor.simple.expense.tracker.data.repository.InternalUserRepository;
import com.github.arhor.simple.expense.tracker.exception.EntityNotFoundException;
import com.github.arhor.simple.expense.tracker.model.ExpenseRequestDTO;
import com.github.arhor.simple.expense.tracker.model.ExpenseResponseDTO;
import com.github.arhor.simple.expense.tracker.service.ExpenseItemService;
import com.github.arhor.simple.expense.tracker.service.ExpenseService;
import com.github.arhor.simple.expense.tracker.service.mapping.ExpenseMapper;
import com.github.arhor.simple.expense.tracker.util.TemporalRange;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ExpenseServiceImpl implements ExpenseService {

    private final ExpenseItemService expenseItemService;
    private final ExpenseRepository expenseRepository;
    private final ExpenseMapper expenseMapper;
    private final InternalUserRepository userRepository;

    @Override
    public List<ExpenseResponseDTO> getUserExpenses(final Long userId, final TemporalRange<LocalDate> dateRange) {
        try (var expenses = expenseRepository.findAllByUserId(userId)) {
            return expenses.map(expense ->
                    expenseMapper.mapToDTO(
                        expense,
                        expenseItemService.getExpenseItemsTotal(
                            expense.id(),
                            userId,
                            dateRange
                        )
                    )
                )
                .toList();
        }
    }

    @Override
    public ExpenseResponseDTO getUserExpenseById(
        final Long userId,
        final Long expenseId,
        final TemporalRange<LocalDate> dateRange
    ) {
        return expenseRepository.findById(expenseId)
            .map(entity ->
                expenseMapper.mapToDTO(
                    entity,
                    expenseItemService.getExpenseItemsTotal(
                        expenseId,
                        userId,
                        dateRange
                    )
                )
            )
            .orElseThrow(() -> new EntityNotFoundException("Expense", "expenseId = " + expenseId));
    }

    @Override
    public ExpenseResponseDTO createUserExpense(final Long userId, final ExpenseRequestDTO requestDTO) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("InternalUser", "id=" + userId);
        }

        var expense = expenseMapper.mapToEntity(requestDTO, userId);
        var result = expenseRepository.save(expense);

        return expenseMapper.mapToDTO(result, BigDecimal.ZERO);
    }
}
