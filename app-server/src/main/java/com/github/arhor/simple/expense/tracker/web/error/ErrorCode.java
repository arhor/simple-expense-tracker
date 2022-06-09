package com.github.arhor.simple.expense.tracker.web.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Getter
@RequiredArgsConstructor
@JsonSerialize(using = ErrorCodeSerializer.class)
public enum ErrorCode {
    UNCATEGORIZED             (Type.GEN, 0, "error.server.internal"),

    VALIDATION_FAILED         (Type.VAL, 0, "error.entity.validation.failed"),

    UNAUTHORIZED              (Type.SEC, 0, "error.server.unauthorized"),

    NOT_FOUND                 (Type.DAT, 0, "error.entity.not.found"),
    DUPLICATE                 (Type.DAT, 1, "error.entity.duplicate"),

    HANDLER_NOT_FOUND_DEFAULT (Type.SRV, 0, "error.server.handler.not.found.default"),
    HANDLER_NOT_FOUND         (Type.SRV, 1, "error.server.handler.not.found"),
    METHOD_ARG_TYPE_MISMATCH  (Type.SRV, 2, "error.value.type.mismatch"),
    ;

    private final Type type;
    private final int numericValue;
    private final String label;

    @Getter
    @RequiredArgsConstructor
    public enum Type {
        GEN("GENERAL"),
        SEC("SECURITY"),
        VAL("VALIDATION"),
        DAT("DATA"),
        SRV("SERVER"),
        ;

        private final String description;
    }
}
