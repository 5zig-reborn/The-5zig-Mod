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
import eu.the5zig.mod.event.EventHandler;
import eu.the5zig.mod.event.WorldTickEvent;
import eu.the5zig.mod.util.Keyboard;

public class ChatSearchManager {

	private boolean searching;
	private boolean keyPressed;

	public ChatSearchManager() {
		The5zigMod.getListener().registerListener(this);
	}

	@EventHandler
	public void onTick(WorldTickEvent event) {
		if (The5zigMod.getVars().isChatOpened()) {
			boolean down = The5zigMod.isCtrlKeyDown() && Keyboard.isKeyDown(Keyboard.KEY_F);
			if (!keyPressed && down) {
				searching = !searching;
				keyPressed = true;
			} else if (!down) {
				keyPressed = false;
			}
		} else {
			searching = false;
		}
	}

	public boolean isSearching() {
		return searching;
	}

	public String getSearchText() {
		return The5zigMod.getVars().getChatBoxText();
	}
}
