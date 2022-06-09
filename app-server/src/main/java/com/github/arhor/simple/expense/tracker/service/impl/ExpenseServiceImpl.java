package com.github.arhor.simple.expense.tracker.service.impl;

import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.arhor.simple.expense.tracker.data.model.ExpenseItem;
import com.github.arhor.simple.expense.tracker.data.repository.ExpenseItemRepository;
import com.github.arhor.simple.expense.tracker.data.repository.ExpenseRepository;
import com.github.arhor.simple.expense.tracker.data.repository.UserRepository;
import com.github.arhor.simple.expense.tracker.exception.EntityNotFoundException;
import com.github.arhor.simple.expense.tracker.model.ExpenseDTO;
import com.github.arhor.simple.expense.tracker.model.ExpenseItemDTO;
import com.github.arhor.simple.expense.tracker.service.ExpenseService;
import com.github.arhor.simple.expense.tracker.service.mapping.ExpenseConverter;
import com.github.arhor.simple.expense.tracker.service.mapping.ExpenseItemConverter;

import static java.time.temporal.TemporalAdjusters.firstDayOfMonth;
import static java.time.temporal.TemporalAdjusters.lastDayOfMonth;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ExpenseServiceImpl implements ExpenseService {

    private final UserRepository userRepository;
    private final ExpenseRepository expenseRepository;
    private final ExpenseItemRepository expenseItemRepository;
    private final ExpenseConverter expenseConverter;
    private final ExpenseItemConverter expenseItemConverter;

    @Override
    public List<ExpenseDTO> getUserExpenses(final Long userId) {
        var expenses = expenseRepository.findByUserId(userId);

        if (expenses.isEmpty()) {
            return Collections.emptyList();
        }

        var result = new ArrayList<ExpenseDTO>(expenses.size());

        for (var expense : expenses) {
            var currentDateTimeUTC = LocalDateTime.now(ZoneOffset.UTC);

            var startDate = currentDateTimeUTC.with(firstDayOfMonth());
            var endDate = currentDateTimeUTC.with(lastDayOfMonth());

            var expenseItems =
                expenseItemRepository.findByExpenseIdAndDateRange(
                    expense.getId(),
                    startDate,
                    endDate
                );

            // TODO: distribute total across the currencies
            var totalAmount =
                expenseItems.stream()
                    .map(ExpenseItem::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            var expenseDTO = expenseConverter.mapEntityToDto(expense);
            expenseDTO.setTotal(totalAmount);
            result.add(expenseDTO);
        }
        return result;
    }

    @Override
    public ExpenseDTO createUserExpense(final Long userId, final ExpenseDTO dto) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("InternalUser", "id=" + userId);
        }

        var expense = expenseConverter.mapDtoToEntity(dto);
        expense.setUserId(userId);
        var result = expenseRepository.save(expense);

        return expenseConverter.mapEntityToDto(result);
    }

    @Override
    public ExpenseItemDTO createExpenseItem(final Long expenseId, final ExpenseItemDTO dto) {

        return null;
    }
}
