package com.github.arhor.simple.expense.tracker.service.impl;

import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.arhor.simple.expense.tracker.data.repository.ExpenseRepository;
import com.github.arhor.simple.expense.tracker.data.repository.UserRepository;
import com.github.arhor.simple.expense.tracker.exception.EntityNotFoundException;
import com.github.arhor.simple.expense.tracker.model.ExpenseDetailsResponseDTO;
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
    private final UserRepository userRepository;

    @Override
    public List<ExpenseResponseDTO> getUserExpenses(final Long userId, final TemporalRange<LocalDate> dateRange) {
        try (var expenses = expenseRepository.findAllByUserId(userId)) {
            return expenses
                .map(expenseMapper::mapToDTO)
                .peek(dto -> dto.setTotal(expenseItemService.getExpenseItemsTotal(dto.getId(), userId, dateRange)))
                .toList();
        }
    }

    @Override
    public ExpenseDetailsResponseDTO getUserExpenseById(
        final Long userId,
        final Long expenseId,
        final TemporalRange<LocalDate> dateRange
    ) {
        var responseDTO = expenseRepository.findById(expenseId)
            .map(expenseMapper::mapToDetailsDTO)
            .orElseThrow(() -> new EntityNotFoundException("Expense", "expenseId = " + expenseId));

        var result = expenseItemService.getExpenseItemsTotalWithDTOs(expenseId, userId, dateRange);

        responseDTO.setItems(result.items());
        responseDTO.setTotal(result.total());

        return responseDTO;
    }

    @Override
    public ExpenseResponseDTO createUserExpense(final Long userId, final ExpenseRequestDTO requestDTO) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("InternalUser", "id=" + userId);
        }

        var expense = expenseMapper.mapToEntity(requestDTO, userId);
        var result = expenseRepository.save(expense);

        return expenseMapper.mapToDTO(result);
    }
}
