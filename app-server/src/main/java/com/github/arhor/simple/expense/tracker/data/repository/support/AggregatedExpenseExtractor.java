package com.github.arhor.simple.expense.tracker.data.repository.support;

import lombok.extern.slf4j.Slf4j;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import com.github.arhor.simple.expense.tracker.data.model.projection.AggregatedExpenseItemProjection;
import com.github.arhor.simple.expense.tracker.data.model.projection.AggregatedExpenseProjection;

@Slf4j
@Component(AggregatedExpenseExtractor.BEAN_NAME)
public class AggregatedExpenseExtractor implements ResultSetExtractor<List<AggregatedExpenseProjection>> {

    public static final String BEAN_NAME = "aggregatedExpenseExtractor";

    // @formatter:off
    private static final String COL_ID           = "id";
    private static final String COL_USER_ID      = "user_id";
    private static final String COL_NAME         = "name";
    private static final String COL_ICON         = "icon";
    private static final String COL_COLOR        = "color";
    private static final String COL_DATE         = "date";
    private static final String COL_CURRENCY     = "currency";
    private static final String COL_TOTAL_AMOUNT = "total_amount";
    // @formatter:on

    @Override
    public List<AggregatedExpenseProjection> extractData(final ResultSet rs) throws SQLException, DataAccessException {
        List<AggregatedExpenseProjection> result = null;
        List<AggregatedExpenseItemProjection> currentExpenseItems = null;

        var prevExpenseId = -1L;

        while (rs.next()) {
            var currExpenseId = rs.getLong(COL_ID);

            if (currExpenseId != prevExpenseId) {
                currentExpenseItems = new ArrayList<>();

                if (result == null) {
                    result = new ArrayList<>();
                }
                result.add(
                    new AggregatedExpenseProjection(
                        currExpenseId,
                        rs.getLong(COL_USER_ID),
                        rs.getString(COL_NAME),
                        rs.getString(COL_ICON),
                        rs.getString(COL_COLOR),
                        currentExpenseItems
                    )
                );
                prevExpenseId = currExpenseId;
            }
            if (currentExpenseItems != null) {
                var expenseItem = extractExpenseItem(rs);

                if (expenseItem != null) {
                    currentExpenseItems.add(expenseItem);
                }
            }
        }
        return (result != null) ? result : Collections.emptyList();
    }

    private AggregatedExpenseItemProjection extractExpenseItem(final ResultSet rs)
        throws SQLException {

        final var date = rs.getDate(COL_DATE);
        final var currency = rs.getString(COL_CURRENCY);
        final var totalAmount = rs.getBigDecimal(COL_TOTAL_AMOUNT);

        if (date != null && currency != null && totalAmount != null) {
            return new AggregatedExpenseItemProjection(
                date.toLocalDate(),
                currency,
                totalAmount
            );
        } else if (date != null || currency != null || totalAmount != null) {
            log.debug(
                "Unexpected null value found during expense item extraction - {}: {}, {}: {}, {}: {}",
                COL_DATE, date,
                COL_CURRENCY, currency,
                COL_TOTAL_AMOUNT, totalAmount
            );
        }
        return null;
    }
}
