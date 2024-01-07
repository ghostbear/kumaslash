CREATE TABLE IF NOT EXISTS guild(
    snowflake BIGINT PRIMARY KEY
);

CREATE TABLE IF NOT EXISTS rules(
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    guild_snowflake BIGINT REFERENCES guild(snowflake) ON DELETE CASCADE,
    number DOUBLE PRECISION NOT NULL,
    short TEXT NOT NULL,
    long TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS socials_action(
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    guild_snowflake BIGSERIAL REFERENCES guild(snowflake) ON DELETE CASCADE,
    action TEXT,
    template TEXT,
    UNIQUE (guild_snowflake, action)
);

CREATE TABLE IF NOT EXISTS socials(
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    socials_action_id UUID REFERENCES socials_action(id) ON DELETE CASCADE,
    url TEXT NOT NULL,
    UNIQUE (socials_action_id, url)
);
