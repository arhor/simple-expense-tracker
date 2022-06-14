package com.github.arhor.simple.expense.tracker.service.impl;

import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.money.Monetary;

import org.javamoney.moneta.FastMoney;
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

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ExpenseServiceImpl implements ExpenseService {

    private final UserRepository userRepository;
    private final ExpenseRepository expenseRepository;
    private final ExpenseItemRepository expenseItemRepository;
    private final ExpenseConverter expenseConverter;
    private final MoneyConverter moneyConverter;

    @Override
    public List<ExpenseResponseDTO> getUserExpenses(final Long userId, final TemporalRange<LocalDate> dateRange) {
        var expenses = expenseRepository.findByUserId(userId);

        if (expenses.isEmpty()) {
            return Collections.emptyList();
        }

        var result = new ArrayList<ExpenseResponseDTO>(expenses.size());

        for (var expense : expenses) {
            var responseDTO = expenseConverter.mapToDTO(expense);

            initializeExpenseTotal(responseDTO, userId, dateRange);

            result.add(responseDTO);
        }
        return result;
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
        // TODO: implement
        return null;
    }

    private void initializeExpenseTotal(
        final ExpenseResponseDTO expense,
        final Long userId,
        final TemporalRange<LocalDate> dateRange
    ) {
        final var targetCurrency = userRepository.findById(userId)
            .map(InternalUser::getCurrency)
            .map(Monetary::getCurrency)
            .orElseThrow(() -> new EntityNotFoundException("User", "id=" + userId));

        final var expenseItems = expenseItemRepository.findByExpenseIdAndDateRange(
            expense.getId(),
            dateRange.start(),
            dateRange.end()
        );

        var total = Money.zero(targetCurrency);
        for (final var item : expenseItems) {
            final var amount = FastMoney.of(item.getAmount(), item.getCurrency());
            final var convertedAmount = moneyConverter.convert(amount, targetCurrency, item.getDate());

            total = total.add(convertedAmount);
        }
        expense.setTotal(total.getNumberStripped());
    }
}
