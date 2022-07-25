package com.github.arhor.simple.expense.tracker.service.impl;

import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

import javax.money.CurrencyUnit;
import javax.money.Monetary;

import org.javamoney.moneta.Money;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.arhor.simple.expense.tracker.data.model.ExpenseItem;
import com.github.arhor.simple.expense.tracker.data.model.InternalUser;
import com.github.arhor.simple.expense.tracker.data.repository.ExpenseItemRepository;
import com.github.arhor.simple.expense.tracker.data.repository.ExpenseRepository;
import com.github.arhor.simple.expense.tracker.data.repository.UserRepository;
import com.github.arhor.simple.expense.tracker.exception.EntityNotFoundException;
import com.github.arhor.simple.expense.tracker.model.ExpenseDetailsResponseDTO;
import com.github.arhor.simple.expense.tracker.model.ExpenseItemDTO;
import com.github.arhor.simple.expense.tracker.model.ExpenseRequestDTO;
import com.github.arhor.simple.expense.tracker.model.ExpenseResponseDTO;
import com.github.arhor.simple.expense.tracker.service.ExpenseService;
import com.github.arhor.simple.expense.tracker.service.MoneyConverter;
import com.github.arhor.simple.expense.tracker.service.TimeService.TemporalRange;
import com.github.arhor.simple.expense.tracker.service.mapping.ExpenseMapper;
import com.github.arhor.simple.expense.tracker.service.mapping.ExpenseItemMapper;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ExpenseServiceImpl implements ExpenseService {

    private final UserRepository userRepository;
    private final ExpenseRepository expenseRepository;
    private final ExpenseItemRepository expenseItemRepository;
    private final ExpenseMapper expenseMapper;
    private final ExpenseItemMapper expenseItemMapper;
    private final MoneyConverter converter;

    @Override
    public List<ExpenseResponseDTO> getUserExpenses(final Long userId, final TemporalRange<LocalDate> dateRange) {
        var targetCurrency = getUserCurrency(userId);

        try (var expenses = expenseRepository.findByUserId(userId)) {
            return expenses
                .map(expenseMapper::mapToDTO)
                .peek(expense -> {
                    useExpenseItemsStream(expense.getId(), dateRange, stream -> {
                        var calculator = new TotalCalculator(targetCurrency);
                        stream.forEach(calculator::add);
                        expense.setTotal(calculator.total.getNumberStripped());
                    });
                })
                .toList();
        }
    }

    @Override
    public ExpenseDetailsResponseDTO getUserExpenseById(
        final Long userId,
        final Long expenseId,
        final TemporalRange<LocalDate> dateRange
    ) {
        var responseDTO = expenseRepository.findByUserIdAndExpenseId(userId, expenseId)
            .map(expenseMapper::mapToDetailsDTO)
            .orElseThrow(() -> new EntityNotFoundException("Expense", "userId=" + userId + ", expenseId=" + expenseId));

        var targetCurrency = getUserCurrency(userId);

        useExpenseItemsStream(expenseId, dateRange, stream -> {
            var calculator = new TotalCalculator(targetCurrency);
            var items = stream.peek(calculator::add).map(expenseItemMapper::mapToDTO).toList();

            responseDTO.setItems(items);
            responseDTO.setTotal(calculator.total.getNumberStripped());
        });

        return responseDTO;
    }

    @Override
    public ExpenseResponseDTO createUserExpense(final Long userId, final ExpenseRequestDTO requestDTO) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("InternalUser", "id=" + userId);
        }

        var expense = expenseMapper.mapToEntity(requestDTO);
        expense.setUserId(userId);
        var result = expenseRepository.save(expense);

        return expenseMapper.mapToDTO(result);
    }

    @Override
    public ExpenseItemDTO createExpenseItem(final Long expenseId, final ExpenseItemDTO dto) {
        if (!expenseRepository.existsById(expenseId)) {
            throw new EntityNotFoundException("Expense", "id=" + expenseId);
        }

        var expenseItem = expenseItemMapper.mapToEntity(dto);
        expenseItem.setExpenseId(expenseId);
        var result = expenseItemRepository.save(expenseItem);

        return expenseItemMapper.mapToDTO(result);
    }

    private CurrencyUnit getUserCurrency(final Long userId) {
        return userRepository.findById(userId)
            .map(InternalUser::getCurrency)
            .map(Monetary::getCurrency)
            .orElseThrow(() -> new EntityNotFoundException("User", "id=" + userId));
    }

    private void useExpenseItemsStream(
        final Long expenseId,
        final TemporalRange<LocalDate> dateRange,
        final Consumer<Stream<ExpenseItem>> action
    ) {
        try (
            var stream = expenseItemRepository.findByExpenseIdAndDateRange(
                expenseId,
                dateRange.start(),
                dateRange.end()
            )
        ) {
            action.accept(stream);
        }
    }

    private final class TotalCalculator {
        private Money total;

        TotalCalculator(final CurrencyUnit currency) {
            total = Money.zero(currency);
        }

        public void add(final ExpenseItem expense) {
            var sourceCurrency = expense.getCurrency();
            var targetCurrency = total.getCurrency();
            var amount = Money.of(expense.getAmount(), sourceCurrency);
            var date = expense.getDate();

            total = total.add(converter.convert(amount, targetCurrency, date));
        }
    }
}
