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

package eu.the5zig.mod.modules.items.player;

import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.manager.AFKManager;
import eu.the5zig.mod.modules.StringItem;
import eu.the5zig.util.Utils;
import eu.the5zig.util.minecraft.ChatColor;

public class AFKTime extends StringItem {
	@Override
	protected Object getValue(boolean dummy) {
		if (dummy) {
			return Utils.convertToClock(1000 * 60);
		}
		if (The5zigMod.getDataManager().getAfkManager().getAFKTime() > AFKManager.AFK_COUNTER) {
			return Utils.convertToClock(The5zigMod.getDataManager().getAfkManager().getAFKTime());
		}
		if (The5zigMod.getDataManager().getAfkManager().getLastAfkTime() != 0) {
			return ChatColor.UNDERLINE + Utils.convertToClock(The5zigMod.getDataManager().getAfkManager().getLastAfkTime());
		}
		return null;
	}

	@Override
	public String getTranslation() {
		return "ingame.afk";
	}
}
