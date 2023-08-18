CREATE TABLE IF NOT EXISTS guild (
    snowflake BIGSERIAL PRIMARY KEY
);

CREATE TYPE social_action AS ENUM ('BITE', 'HUG', 'KISS', 'LICK', 'NUZZLE', 'SLAP', 'HEADPAT');

CREATE TABLE IF NOT EXISTS guild_social (
    uuid UUID DEFAULT gen_random_uuid(),
    fk_guild_snowflake BIGSERIAL REFERENCES guild(snowflake),
    image_url VARCHAR NOT NULL,
    action social_action,
    PRIMARY KEY (uuid, fk_guild_snowflake)
);

CREATE TYPE log_type AS ENUM ('TIMEOUT', 'RULE');

CREATE TABLE IF NOT EXISTS guild_log_channel (
    channel_snowflake BIGSERIAL PRIMARY KEY,
    fk_guild_snowflake BIGSERIAL REFERENCES guild(snowflake),
    type log_type NOT NULL,
    UNIQUE (channel_snowflake, fk_guild_snowflake, type)
);

CREATE TABLE IF NOT EXISTS guild_rule (
    uuid UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    fk_guild_snowflake BIGSERIAL REFERENCES guild(snowflake),
    index SMALLINT,
    title VARCHAR,
    description VARCHAR,
    UNIQUE (fk_guild_snowflake, index)
);
