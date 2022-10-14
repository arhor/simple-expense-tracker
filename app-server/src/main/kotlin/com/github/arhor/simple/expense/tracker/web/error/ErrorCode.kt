package com.github.arhor.simple.expense.tracker.web.error;

import com.fasterxml.jackson.databind.annotation.JsonSerialize

@JsonSerialize(using = ErrorCodeSerializer::class)
enum class ErrorCode(
    val type: Type,
    val value: Int,
    val label: String,
) {
    // @formatter:off
    UNCATEGORIZED             (Type.GEN, 0x00000, "error.server.internal"),
    HANDLER_NOT_FOUND_DEFAULT (Type.GEN, 0x00001, "error.server.handler.not.found.default"),
    HANDLER_NOT_FOUND         (Type.GEN, 0x00002, "error.server.handler.not.found"),
    METHOD_ARG_TYPE_MISMATCH  (Type.GEN, 0x00003, "error.server.value.type.mismatch"),
    FILE_NOT_FOUND            (Type.GEN, 0x00004, "error.server.file.not.found"),

    VALIDATION_FAILED         (Type.VAL, 0x00000, "error.entity.validation.failed"),

    UNAUTHORIZED              (Type.SEC, 0x00000, "error.server.unauthorized"),

    DATA_COMMON               (Type.DAT, 0x00000, "error.data.common"),
    NOT_FOUND                 (Type.DAT, 0x00001, "error.entity.not.found"),
    DUPLICATE                 (Type.DAT, 0x00002, "error.entity.duplicate"),
    // @formatter:on
    ;

    enum class Type {
        /**
         * General.
         *
         * The most common category which includes any issues that do
         * not meet the requirements for the rest of the types.
         */
        GEN,

        /**
         * Security.
         *
         * This category includes any issue caused by insufficient access rights.
         */
        SEC,

        /**
         * Validation.
         *
         * This category includes issues related to incoming data validation - incorrect
         * user input, etc.
         */
        VAL,

        /**
         * Data.
         *
         * This category includes all types of issues related to the data representing
         * the application's state - lack of requested data, conflicts when modifying
         * existing data, etc.
         */
        DAT,
        ;
    }
}
