-- extension 'uuid-ossp' >>> START
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
-- extension 'uuid-ossp' <<< END

-- table 'notifications' >>> START
CREATE TABLE IF NOT EXISTS "notifications"
(
    "id"         UUID         NOT NULL PRIMARY KEY DEFAULT UUID_GENERATE_V4(),
    "severity"   VARCHAR(10)  NOT NULL,
    "message"    VARCHAR(500) NOT NULL,
    "timestamp"  TIMESTAMP    NOT NULL,
    "user_id"    BIGINT       NOT NULL,
    "created_by" BIGINT       NULL,

    CONSTRAINT FK_notifications_users FOREIGN KEY ("user_id")
        REFERENCES "users" ("id")
        ON UPDATE CASCADE
        ON DELETE CASCADE
) WITH (OIDS = FALSE);
-- table 'notifications' <<< END
