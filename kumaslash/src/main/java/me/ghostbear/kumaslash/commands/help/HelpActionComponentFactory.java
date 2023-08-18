package me.ghostbear.kumaslash.commands.help;

import discord4j.core.object.component.ActionComponent;
import discord4j.core.object.component.TextInput;

public class HelpActionComponentFactory {

	public ActionComponent[] create() {
		return new ActionComponent[] {
				TextInput.small("first", "What version of the app are you on?")
						.placeholder("Tachiyomi 1.13.3"),
				TextInput.small("second", "What source are you having issues with?")
						.placeholder("Example: MangaDex 1.2.158"),
				TextInput.small("third", "What device are you using?")
						.placeholder("Example: Google Pixel 6"),
				TextInput.small("fourth", "What Android version are you on?")
						.placeholder("Example: Android 12L"),
				TextInput.paragraph("fifth", "What issue are you having?", 10, 500)
						.placeholder("Please explain your issue here in detail and include the error if there is any")
		};
	}

}
