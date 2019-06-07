/*
 * Original: Copyright (c) 2015-2019 5zig [MIT]
 * Current: Copyright (c) 2019 5zig Reborn [GPLv3+]
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

import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.chat.entity.Conversation;
import eu.the5zig.mod.event.EventHandler;
import eu.the5zig.mod.event.TickEvent;
import eu.the5zig.mod.gui.GuiConversations;
import eu.the5zig.mod.util.Display;

/**
 * Created by 5zig.
 * All rights reserved © 2015
 */
public class DisplayFocusListener {

	private boolean waitForFocus;

	@EventHandler
	public void onTick(TickEvent event) {
		if (waitForFocus && Display.isActive()) {
			if (The5zigMod.getVars().getCurrentScreen() instanceof GuiConversations) {
				GuiConversations gui = (GuiConversations) The5zigMod.getVars().getCurrentScreen();
				Conversation conversation = gui.getSelectedConversation();
				if (conversation != null) {
					The5zigMod.getConversationManager().setConversationRead(conversation, true);
				}
			}
			waitForFocus = false;
		} else if (!waitForFocus && !Display.isActive()) {
			waitForFocus = true;
		}
	}

}
