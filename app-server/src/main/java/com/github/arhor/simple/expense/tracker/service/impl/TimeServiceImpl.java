package com.github.arhor.simple.expense.tracker.service.impl;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.TimeZone;

import org.springframework.stereotype.Service;

import com.github.arhor.simple.expense.tracker.service.TimeService;

@Service
public class TimeServiceImpl implements TimeService {

    @Override
    public Instant now() {
        return currentInstant(ZoneOffset.UTC);
    }

    @Override
    public Instant now(final TimeZone timeZone) {
        return currentInstant(
            (timeZone != null)
                ? timeZone.toZoneId()
                : ZoneOffset.UTC
        );
    }

    @Override
    public Instant weekAgo() {
        return now().minus(Duration.ofDays(7));
    }

    private Instant currentInstant(final ZoneId zoneId) {
        return Clock.system(zoneId).instant().truncatedTo(ChronoUnit.MILLIS);
    }
}
