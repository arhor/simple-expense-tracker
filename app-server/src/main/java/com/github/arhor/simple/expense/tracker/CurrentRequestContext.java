package com.github.arhor.simple.expense.tracker;

import java.util.Optional;
import java.util.UUID;

public interface CurrentRequestContext {

    UUID getRequestId();

    void setRequestId(UUID requestId);

    boolean isExceptionLogged(Throwable throwable);

    void setExceptionBeenLogged(Throwable throwable);
}
