# Guild

## Slash Commands

### /user avatar :target :guild?

Get the avatar of the targeted user.

### /user banner :target

Get the banner of the targeted user.

### /rule :id? :target?

Get the rules of the guild. 
If id is provided only that rule with be shown and/or a target is provided they will get mentioned.

#### /guild rule view

View the rules in the guild.

#### /guild rule add :id :title :description:

Add a new rule to the guild.

#### /guild rule delete :id

Delete an existing rule from the guild.

#### /guild rule modify :id :title? :description?

Modify an existing rule in the guild.

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
