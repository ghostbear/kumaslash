/*
 * Copyright (C) 2023 ghostbear
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package kumaslash.jda.events;

import java.util.function.Supplier;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public interface CommandSupplier extends Supplier<CommandData> {}
