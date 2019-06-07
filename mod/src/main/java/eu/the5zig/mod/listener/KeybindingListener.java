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
import eu.the5zig.mod.config.items.BoolItem;
import eu.the5zig.mod.event.EventHandler;
import eu.the5zig.mod.event.KeyPressEvent;
import eu.the5zig.mod.event.TickEvent;
import eu.the5zig.mod.gui.GuiCoordinatesClipboard;
import eu.the5zig.mod.gui.GuiHypixelStats;
import eu.the5zig.mod.gui.GuiRaidCalculator;
import eu.the5zig.mod.manager.KeybindingManager;

/**
 * Created by 5zig.
 * All rights reserved © 2015
 */
public class KeybindingListener {

	private int lastPressed = 0;

	@EventHandler
	public void onTick(TickEvent event) {
		KeybindingManager keybindingManager = The5zigMod.getKeybindingManager();

		if (keybindingManager.toggleMod.callIsPressed()) {
			The5zigMod.getConfig().get("showMod", BoolItem.class).next();
			The5zigMod.getConfig().save();
		}
		if (keybindingManager.saveCoords.callIsPressed()) {
			The5zigMod.getVars().displayScreen(new GuiCoordinatesClipboard(null));
		}
		if (keybindingManager.raidTracker.callIsPressed()) {
			The5zigMod.getVars().displayScreen(new GuiRaidCalculator(null));
		}
		if (lastPressed > 0)
			lastPressed--;
	}

	@EventHandler
	public void onKeyPress(KeyPressEvent event) {
		if (event.getKeyCode() != -1 && event.getKeyCode() == The5zigMod.getKeybindingManager().hypixel.callGetKeyCode() && lastPressed++ == 0)
			The5zigMod.getVars().displayScreen(new GuiHypixelStats(The5zigMod.getVars().createWrappedGui(The5zigMod.getVars().getMinecraftScreen())));
	}
}
