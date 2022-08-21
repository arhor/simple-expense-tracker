package com.github.arhor.simple.expense.tracker.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.val;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.money.CurrencyUnit;
import javax.money.Monetary;

import org.javamoney.moneta.Money;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.arhor.simple.expense.tracker.data.model.Expense;
import com.github.arhor.simple.expense.tracker.data.model.InternalUser;
import com.github.arhor.simple.expense.tracker.data.model.projection.AggregatedExpenseItemProjection;
import com.github.arhor.simple.expense.tracker.data.repository.ExpenseItemRepository;
import com.github.arhor.simple.expense.tracker.data.repository.ExpenseRepository;
import com.github.arhor.simple.expense.tracker.data.repository.InternalUserRepository;
import com.github.arhor.simple.expense.tracker.exception.EntityNotFoundException;
import com.github.arhor.simple.expense.tracker.model.ExpenseRequestDTO;
import com.github.arhor.simple.expense.tracker.model.ExpenseResponseDTO;
import com.github.arhor.simple.expense.tracker.service.ExpenseService;
import com.github.arhor.simple.expense.tracker.service.mapping.ExpenseMapper;
import com.github.arhor.simple.expense.tracker.service.money.MoneyConverter;
import com.github.arhor.simple.expense.tracker.util.TemporalRange;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ExpenseServiceImpl implements ExpenseService {

    private final ExpenseMapper expenseMapper;
    private final ExpenseRepository expenseRepository;
    private final ExpenseItemRepository expenseItemRepository;
    private final InternalUserRepository userRepository;
    private final MoneyConverter converter;

    @Override
    public List<ExpenseResponseDTO> getUserExpenses(final Long userId, final TemporalRange<LocalDate> dateRange) {
        val expenses = expenseRepository.findAllByUserId(userId);
        val expenseTotalsById = getExpenseItemsTotal(userId, expenses.stream().map(Expense::getId).toList(), dateRange);

        return expenses
            .stream()
            .map(expense -> expenseMapper.mapToDTO(
                    expense,
                    expenseTotalsById.getOrDefault(
                        expense.getId(),
                        BigDecimal.ZERO
                    )
                )
            )
            .toList();
    }

    @Override
    public ExpenseResponseDTO getExpenseById(final Long expenseId, final TemporalRange<LocalDate> dateRange) {
        return expenseRepository.findById(expenseId)
            .map(entity -> expenseMapper.mapToDTO(
                    entity,
                    getExpenseItemsTotal(entity.getUserId(), List.of(entity.getId()), dateRange).getOrDefault(
                        entity.getId(),
                        BigDecimal.ZERO
                    )
                )
            )
            .orElseThrow(() -> new EntityNotFoundException("Expense", "id=" + expenseId));
    }

    @Override
    public ExpenseResponseDTO createUserExpense(final Long userId, final ExpenseRequestDTO requestDTO) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("InternalUser", "id=" + userId);
        }

        val expense = expenseMapper.mapToEntity(requestDTO, userId);
        val result = expenseRepository.save(expense);

        return expenseMapper.mapToDTO(result, BigDecimal.ZERO);
    }

    private CurrencyUnit getUserCurrency(final Long userId) {
        return userRepository.findById(userId)
            .map(InternalUser::currency)
            .map(Monetary::getCurrency)
            .orElseThrow(() -> new EntityNotFoundException("User", "id = " + userId));
    }

    public Map<Long, BigDecimal> getExpenseItemsTotal(
        final Long userId,
        final Collection<Long> expenseIds,
        final TemporalRange<LocalDate> dateRange
    ) {
        if (expenseIds.isEmpty()) {
            return Collections.emptyMap();
        }
        val targetCurrency = getUserCurrency(userId);
        val totalByExpense = new HashMap<Long, TotalCalculationContext>();

        val aggregatedExpenseItems = expenseItemRepository.findAllAggregatedByExpenseIdsAndDateRange(
            expenseIds,
            dateRange.start(),
            dateRange.end()
        );

        for (val expenseItem : aggregatedExpenseItems) {
            val expenseTotalCalculator = totalByExpense.computeIfAbsent(
                expenseItem.getExpenseId(),
                key -> new TotalCalculationContext(targetCurrency)
            );
            expenseTotalCalculator.add(expenseItem);
        }

        val result = new HashMap<Long, BigDecimal>();
        totalByExpense.forEach((expenseId, context) -> {
            result.put(expenseId, context.total.getNumberStripped());
        });
        return result;
    }

    /**
     * Money total calculation context. Convenient to use with streams since it allows mutations as a side effect.
     */
    private final class TotalCalculationContext {
        private Money total;

        TotalCalculationContext(final CurrencyUnit currency) {
            total = Money.zero(currency);
        }

        void add(final AggregatedExpenseItemProjection expenseItem) {
            val sourceCurrency = expenseItem.getCurrency();
            val targetCurrency = total.getCurrency();

            val amount = Money.of(expenseItem.getTotalAmount(), sourceCurrency);
            val result = converter.convert(amount, targetCurrency, expenseItem.getDate());

            total = total.add(result);
        }
    }
}
