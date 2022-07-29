package com.github.arhor.simple.expense.tracker.service.impl;

import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.function.Function;
import java.util.stream.Stream;

import javax.money.CurrencyUnit;
import javax.money.Monetary;

import org.javamoney.moneta.Money;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.arhor.simple.expense.tracker.data.model.ExpenseItem;
import com.github.arhor.simple.expense.tracker.data.model.InternalUser;
import com.github.arhor.simple.expense.tracker.data.repository.ExpenseItemRepository;
import com.github.arhor.simple.expense.tracker.data.repository.InternalUserRepository;
import com.github.arhor.simple.expense.tracker.exception.EntityNotFoundException;
import com.github.arhor.simple.expense.tracker.model.ExpenseItemDTO;
import com.github.arhor.simple.expense.tracker.service.ExpenseItemService;
import com.github.arhor.simple.expense.tracker.service.MoneyConverter;
import com.github.arhor.simple.expense.tracker.service.mapping.ExpenseItemMapper;
import com.github.arhor.simple.expense.tracker.util.TemporalRange;

import static com.github.arhor.simple.expense.tracker.util.StreamUtils.useStream;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ExpenseItemServiceImpl implements ExpenseItemService {

    private final ExpenseItemRepository expenseItemRepository;
    private final ExpenseItemMapper expenseItemMapper;
    private final InternalUserRepository userRepository;
    private final MoneyConverter converter;

    @Override
    public ExpenseItemDTO createExpenseItem(final Long expenseId, final ExpenseItemDTO dto) {
        var expenseItem = expenseItemMapper.mapToEntity(dto, expenseId);
        var result = expenseItemRepository.save(expenseItem);

        return expenseItemMapper.mapToDTO(result);
    }

    @Override
    public BigDecimal getExpenseItemsTotal(
        final Long expenseId,
        final Long userId,
        final TemporalRange<LocalDate> dateRange
    ) {
        var targetCurrency = getUserCurrency(userId);

        return useExpenseItemsStream(expenseId, dateRange, stream -> {
            var calculator = new TotalCalculationContext(targetCurrency);
            stream.forEach(calculator::add);
            return calculator.total.getNumberStripped();
        });
    }

    @Override
    public ExpenseItems getExpenseItemsTotalWithDTOs(
        final Long expenseId,
        final Long userId,
        final TemporalRange<LocalDate> dateRange
    ) {
        var targetCurrency = getUserCurrency(userId);

        return useExpenseItemsStream(expenseId, dateRange, stream -> {
            var calculator = new TotalCalculationContext(targetCurrency);
            var items = stream.peek(calculator::add).map(expenseItemMapper::mapToDTO).toList();

            return new ExpenseItems(items, calculator.total.getNumberStripped());
        });
    }

    private <R> R useExpenseItemsStream(
        final Long expenseId,
        final TemporalRange<LocalDate> dateRange,
        final Function<Stream<ExpenseItem>, R> action
    ) {
        return useStream(
            () -> expenseItemRepository.findAllByExpenseIdAndDateRange(
                expenseId,
                dateRange.start(),
                dateRange.end()
            ),
            action
        );
    }

    private CurrencyUnit getUserCurrency(final Long userId) {
        return userRepository.findById(userId)
            .map(InternalUser::currency)
            .map(Monetary::getCurrency)
            .orElseThrow(() -> new EntityNotFoundException("User", "id = " + userId));
    }

    /**
     * Money total calculation context. Convenient to use with streams since it allows mutations as side effect.
     */
    private final class TotalCalculationContext {
        private Money total;

        TotalCalculationContext(final CurrencyUnit currency) {
            total = Money.zero(currency);
        }

        void add(final ExpenseItem expense) {
            var sourceCurrency = expense.currency();
            var targetCurrency = total.getCurrency();

            var amount = Money.of(expense.amount(), sourceCurrency);
            var result = converter.convert(amount, targetCurrency, expense.date());

            total = total.add(result);
        }
    }
}
