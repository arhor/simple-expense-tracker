package com.github.arhor.simple.expense.tracker.service;

import java.time.Instant;
import java.util.TimeZone;

public interface TimeService {

    Instant now();

    Instant now(TimeZone timeZone);

    Instant weekAgo();
}
