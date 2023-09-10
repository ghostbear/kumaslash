CREATE FUNCTION insert_default_guild_rule()
    RETURNS TRIGGER
    LANGUAGE plpgsql
    AS
$$
BEGIN
    INSERT INTO guild_rule(fk_guild_snowflake, index, title, description)
    VALUES
    (NEW.snowflake, 1, 'You have to read the following channels.', '<#349477891215589379> & <#738862409284059239>.'),
    (NEW.snowflake, 2, 'You must not ask for support in any non-support channels.', 'Support channels includes <#349436576037732355>, <#566590778323763207> and the forks channels.'),
    (NEW.snowflake, 3, 'You must use the English language outside of the support channels.', 'It''s fine to use another language in the support channels but nowhere else.'),
    (NEW.snowflake, 4, 'Use common sense, don''t behave poorly.', 'This includes but is not limited to: spamming, user mistreatment, DM/pinging unnecessarily, not listening to staff, disguising links, and more.'),
    (NEW.snowflake, 5, 'Follow the Discord community guidelines.', 'We didn''t make these rules but we will enforce them, we don''t make exceptions for anyone. [Read them here!](https://discord.com/guidelines)'),
    (NEW.snowflake, 6, 'You must not circumvent the word filter.', 'It''s set there for a reason.'),
    (NEW.snowflake, 7, 'Spoilable content needs to be marked with context.', 'Not everyone''s definition is the same, you can read ours by using the `!spoilers` tag.'),
    (NEW.snowflake, 8, 'Do not advertise unprompted.', 'This includes posting server invites, advertisements, etc without permission from a staff member. This also includes DMing fellow members.'),
    (NEW.snowflake, 9, 'No impersonation.', 'Especially of members with roles.'),
    (NEW.snowflake, 10, 'No source recommendations.', 'Source recommendations increase the amount of load that extensions get, which causes them to ban us. The only source we are able to recommend with the source''s permission is **MangaDex**.'),
    (NEW.snowflake, 11, 'Your name must be auto-fill compliant.', 'That means it must be possible to get Discord to suggest you just by typing `A-Z`/`0-9` characters after **@**.'),
    (NEW.snowflake, 12, 'Follow channel rules.', 'Some channels have additional rules in the channel topic which you are required to follow.');

    RETURN NEW;
END;
$$;

CREATE TRIGGER new_guild_add_default_guild_rule
    AFTER INSERT
    ON guild
    FOR EACH ROW
    EXECUTE PROCEDURE insert_default_guild_rule();
