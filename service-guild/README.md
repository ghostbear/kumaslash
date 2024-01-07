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
- `rule_id` is choices which is guild specific.
- `target` is an optional to mention a user, used to notify them that they aren't following the rule.

---

```
/rules :target?
```

Get the rules of the guild.
- `target` is an optional to mention a user, used to notify them that they aren't following the rules.

#### Managing Rules

KumaSlash provides a way to add/delete/modify rules for the guild.

---

```
/guild rule add :number :short :long
```

Add a new rule to the guild.
- `number` is the number/index of the rule.
- `short` is the short version of the rule.
- `long` is the long version of the rule.

---

```
/guild rule delete :rule_id
```

Delete an existing rule from the guild
- `rule_id` is the automatically generated id of the rule (this option is autocomplete).

---


```
/guild rule modify :rule_id :number? :title? :description?
```

Modify an existing rule in the guild.
- `rule_id` is the automatically generated id of the rule (this option is autocomplete).
- `number?` is the number/index of the rule (use decimals to insert a rule between two rules).
- `short?` is the short version of the rule.
- `long?` is the long version of the rule.

### Jumbo

```
/jumbo :emoji
```

Make a emoji jumbo sized.

### Social

```
/social :action_id :target
```

Do a social action on a user.
- `action_id` an UUID of the social action to use. This is auto complete which mean that the name of the social action can be used.
- `target` an user you want to be social with, can't be yourself.

#### Managing Social

> A social action need to be created before one can add images to that social action.

```
/guild social-action add :name :template
```

Add a new social action to the guild. A social action works like a group for images.
- `name`  is the name of the social action.
- `template` is the template which will be used when using the social action.

---

```
/guild social-action modify :action_id :name? :template?
```

Modify an existing social action in the guild.
- `action_id` is the UUID of the social action. This is auto complete which mean that the name of the social action can be used.
- `name?` is the name of the social action.
- `template?` is the template which will be used when using the social action.

---

```
/guild social-action delete :action_id 
```

Delete an existing social action from the guild.
- `action_id` is the UUID of the social action. This is auto complete which mean that the name of the social action can be used.

---

```
/guild social add :action_id :url
```

Add an image to a social action.
- `action_id` is the UUID of the social action. This is auto complete which mean that the name of the social action can be used.
- `url` is the url of the image to be added to the social action

---

```
/guild social delete :social_id
```

Add an image to a social action.
- `social_id` is the UUID of the social action. This is auto complete which mean that the name of the social action or url can be used.

---

### /guild moderation_channel :message_channel

Set a channel as the output for moderation actions.

## Event Handler

### Timeout

Will output messages about guild user timeouts in the moderation channel.
