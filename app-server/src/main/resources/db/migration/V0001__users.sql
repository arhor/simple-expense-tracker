-- table 'users' >>> START
CREATE TABLE IF NOT EXISTS "users"
(
    "id"                BIGSERIAL     NOT NULL PRIMARY KEY,
    "username"          VARCHAR(128)  NULL UNIQUE,
    "password"          VARCHAR(1024) NULL,
    "external_id"       VARCHAR(64)   NULL,
    "external_provider" VARCHAR(64)   NULL,
    "created"           TIMESTAMP     NOT NULL,
    "updated"           TIMESTAMP     NULL,
    "deleted"           BOOLEAN       NOT NULL DEFAULT FALSE
) WITH (OIDS = FALSE);
-- table 'users' <<< END
