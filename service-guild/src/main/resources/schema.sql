CREATE TABLE IF NOT EXISTS guild(
    snowflake BIGINT PRIMARY KEY
);

CREATE TABLE IF NOT EXISTS rules(
    id BIGSERIAL PRIMARY KEY,
    guild_snowflake BIGINT REFERENCES guild(snowflake),
    number DOUBLE PRECISION NOT NULL,
    short TEXT NOT NULL,
    long TEXT NOT NULL
);