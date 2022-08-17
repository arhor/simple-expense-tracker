package com.github.arhor.simple.expense.tracker.web.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Getter
@RequiredArgsConstructor
@JsonSerialize(using = ErrorCodeSerializer.class)
public enum ErrorCode {
    // @formatter:off
    UNCATEGORIZED             (Type.GEN, 0x00000, "error.server.internal"),

    VALIDATION_FAILED         (Type.VAL, 0x00000, "error.entity.validation.failed"),

    UNAUTHORIZED              (Type.SEC, 0x00000, "error.server.unauthorized"),

    DATA_COMMON               (Type.DAT, 0x00000, "error.data.common"),
    NOT_FOUND                 (Type.DAT, 0x00001, "error.entity.not.found"),
    DUPLICATE                 (Type.DAT, 0x00002, "error.entity.duplicate"),

    HANDLER_NOT_FOUND_DEFAULT (Type.SRV, 0x00000, "error.server.handler.not.found.default"),
    HANDLER_NOT_FOUND         (Type.SRV, 0x00001, "error.server.handler.not.found"),
    METHOD_ARG_TYPE_MISMATCH  (Type.SRV, 0x00002, "error.value.type.mismatch"),
    FILE_NOT_FOUND            (Type.SRV, 0x00003, "error.server.file.not.found"),
    // @formatter:on
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
