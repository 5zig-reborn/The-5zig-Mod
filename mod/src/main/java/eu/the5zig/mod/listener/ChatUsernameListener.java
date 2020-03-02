/*
 * Copyright (c) 2019-2020 5zig Reborn
 * Copyright (c) 2015-2019 5zig
 *
 * This file is part of The 5zig Mod
 * The 5zig Mod is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The 5zig Mod is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with The 5zig Mod.  If not, see <http://www.gnu.org/licenses/>.
 */

package eu.the5zig.mod.listener;

import eu.the5zig.mod.I18n;
import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.event.ChatEvent;
import eu.the5zig.mod.event.EventHandler;
import eu.the5zig.mod.util.Display;
import eu.the5zig.util.minecraft.ChatColor;

public class ChatUsernameListener {

	@EventHandler
	public boolean onServerChat(ChatEvent event) {
		String message = event.getMessage();
		if (message.contains(The5zigMod.getDataManager().getUsername()) && !Display.isActive() && The5zigMod.getConfig().getBool("notifyOnName")) {
			The5zigMod.getTrayManager().displayMessage("The 5zig Mod - " + I18n.translate("ingame_chat.new_message"), ChatColor.stripColor(message));
		}
		return false;
	}
}
