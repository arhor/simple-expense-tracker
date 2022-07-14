package com.github.arhor.simple.expense.tracker.service.impl;

import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;

import javax.money.Monetary;

import org.javamoney.moneta.Money;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.arhor.simple.expense.tracker.data.model.InternalUser;
import com.github.arhor.simple.expense.tracker.data.repository.ExpenseItemRepository;
import com.github.arhor.simple.expense.tracker.data.repository.ExpenseRepository;
import com.github.arhor.simple.expense.tracker.data.repository.UserRepository;
import com.github.arhor.simple.expense.tracker.exception.EntityNotFoundException;
import com.github.arhor.simple.expense.tracker.model.ExpenseItemDTO;
import com.github.arhor.simple.expense.tracker.model.ExpenseRequestDTO;
import com.github.arhor.simple.expense.tracker.model.ExpenseResponseDTO;
import com.github.arhor.simple.expense.tracker.service.ExpenseService;
import com.github.arhor.simple.expense.tracker.service.MoneyConverter;
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
    private final MoneyConverter moneyConverter;

    @Override
    public List<ExpenseResponseDTO> getUserExpenses(final Long userId, final TemporalRange<LocalDate> dateRange) {
        try (var expenses = expenseRepository.findByUserId(userId)) {
            return expenses.map(expenseConverter::mapToDTO)
                .peek(it -> initializeExpenseTotal(it, userId, dateRange))
                .toList();
        }
    }

    @Override
    public ExpenseResponseDTO getUserExpenseById(
        final Long userId,
        final Long expenseId,
        final TemporalRange<LocalDate> dateRange
    ) {
        var responseDTO = expenseRepository.findByUserIdAndExpenseId(userId, expenseId)
            .map(expenseConverter::mapToDTO)
            .orElseThrow(() -> new EntityNotFoundException("Expense", "userId=" + userId + ", expenseId=" + expenseId));

        initializeExpenseTotal(responseDTO, userId, dateRange);

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
        if (!expenseRepository.existsById(expenseId)) {
            throw new EntityNotFoundException("Expense", "id=" + expenseId);
        }

        var expenseItem = expenseItemConverter.mapToEntity(dto);
        expenseItem.setExpenseId(expenseId);
        var result = expenseItemRepository.save(expenseItem);

        return expenseItemConverter.mapToDTO(result);
    }

    private void initializeExpenseTotal(
        final ExpenseResponseDTO expense,
        final Long userId,
        final TemporalRange<LocalDate> dateRange
    ) {
        var targetCurrency = userRepository.findById(userId)
            .map(InternalUser::getCurrency)
            .map(Monetary::getCurrency)
            .orElseThrow(() -> new EntityNotFoundException("User", "id=" + userId));

        var id = expense.getId();
        var start = dateRange.start();
        var end = dateRange.end();

        // TODO: possible bottleneck, could be improved by moving calculations into the SQL query
        try (var items = expenseItemRepository.findByExpenseIdAndDateRange(id, start, end)) {
            expense.setTotal(
                items.reduce(
                    Money.zero(targetCurrency),
                    (total, item) -> {
                        var expenseAmount = item.getAmount();
                        var expenseCurrency = item.getCurrency();
                        var expenseDate = item.getDate();

                        var amount = Money.of(expenseAmount, expenseCurrency);
                        var convertedAmount = moneyConverter.convert(amount, targetCurrency, expenseDate);

                        return total.add(convertedAmount);
                    },
                    Money::add
                ).getNumberStripped()
            );
        }
    }
}
