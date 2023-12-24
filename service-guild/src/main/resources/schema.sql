CREATE TABLE IF NOT EXISTS guild(
    id bigint PRIMARY KEY
);

CREATE TABLE IF NOT EXISTS shout(
    id bigint PRIMARY KEY,
    snowflake bigint,
    value VARCHAR NOT NULL
);
