# Guild

## Slash Commands

`:name` is a required option.

`:name?` is an optional option.

### User

```
/user avatar :target :guild?
```

Get the avatar of the targeted user.

---

```
/user banner :target
```

Get the banner of the targeted user.

### Rule

```
/rule :rule_id :target?
```

Get a rule of the guild.
`rule_id` is choices which is guild specific.
`target` is an optional to mention a user, used to notify them that they aren't following the rule.

---

```
/rules :target?
```

Get the rules of the guild.
`target` is an optional to mention a user, used to notify them that they aren't following the rules.

#### Managing Rules

KumaSlash provides a way to add/delete/modify rules for the guild.

---

```
/guild rule add :number :short :long
```

Add a new rule to the guild.
`number` is the number/index of the rule.
`short` is the short version of the rule.
`long` is the long version of the rule.

---

```
/guild rule delete :rule_id
```

Delete an existing rule from the guild
`rule_id` is the automatically generated id of the rule (this option is autocomplete).

---


```
/guild rule modify :rule_id :number? :title? :description?
```

Modify an existing rule in the guild.
`rule_id` is the automatically generated id of the rule (this option is autocomplete).
`number?` is the number/index of the rule (use decimals to insert a rule between two rules).
`short?` is the short version of the rule.
`long?` is the long version of the rule.

### /jumbo :emoji

Make a emoji jumbo sized.

### /social :action :target

Do a social interaction with the target.
The target can't be yourself.

#### /guild social action view

View the social actions in the guild.

#### /guild social action add :action

Add a new action to the guild.

#### /guild social action delete :action

Delete an existing action from the guild.

#### /guild social view :action

View the social images for an action in the guild.

#### /guild social add :action :url

Add a social image to an action in the guild.

#### /guild social delete :auto_id

Delete a social image from an action in the guild.

### /guild moderation_channel :message_channel

Set a channel as the output for moderation actions.

## Event Handler

### Timeout

Will output messages about guild user timeouts in the moderation channel.
