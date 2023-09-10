CREATE EXTENSION pg_trgm;

CREATE TABLE IF NOT EXISTS extension (
    package_name VARCHAR PRIMARY KEY,
    name VARCHAR,
    file_name VARCHAR,
    language VARCHAR,
    code SERIAL,
    version VARCHAR,
    is_nsfw BOOLEAN,
    has_readme BOOLEAN,
    has_changelog BOOLEAN
);

CREATE TABLE IF NOT EXISTS source (
    id VARCHAR PRIMARY KEY,
    name VARCHAR,
    language VARCHAR,
    base_url VARCHAR,
    version_id BIGSERIAL,
    has_cloudflare BOOLEAN,
    fk_package_name VARCHAR REFERENCES extension(package_name)
);
