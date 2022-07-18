-- table 'expenses' >>> START
CREATE TABLE IF NOT EXISTS "expenses"
(
    "id"       BIGSERIAL    NOT NULL PRIMARY KEY,
    "name"     VARCHAR(128) NOT NULL,
    "icon"     VARCHAR(64)  NULL,
    "color"    VARCHAR(64)  NULL,
    "user_id"  BIGINT       NOT NULL,
    "created"  TIMESTAMP    NOT NULL,
    "updated"  TIMESTAMP    NULL,
    "deleted"  BOOLEAN      NOT NULL DEFAULT FALSE,

    CONSTRAINT UQ_user_id_name UNIQUE ("user_id", "name"),

    CONSTRAINT FK_expenses_users FOREIGN KEY ("user_id")
        REFERENCES "users" ("id")
        ON UPDATE CASCADE
        ON DELETE CASCADE
) WITH (OIDS = FALSE);
-- table 'expenses' <<< END

-- table 'expense_items' >>> START
CREATE TABLE IF NOT EXISTS "expense_items"
(
    "id"         BIGSERIAL      NOT NULL PRIMARY KEY,
    "date"       DATE           NOT NULL,
    "amount"     DECIMAL(12, 2) NOT NULL,
    "currency"   VARCHAR(3)     NOT NULL,
    "comment"    VARCHAR(128)   NULL,
    "expense_id" BIGINT         NOT NULL,

    CONSTRAINT FK_expense_items_expenses FOREIGN KEY ("expense_id")
        REFERENCES "expenses" ("id")
        ON UPDATE CASCADE
        ON DELETE CASCADE
) WITH (OIDS = FALSE);
-- table 'expense_items' <<< END
