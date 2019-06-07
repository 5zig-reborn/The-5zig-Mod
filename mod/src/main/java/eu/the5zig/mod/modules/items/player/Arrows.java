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

package eu.the5zig.mod.modules.items.player;

import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.gui.ingame.ItemStack;
import eu.the5zig.mod.modules.items.ItemStackItem;

public class Arrows extends ItemStackItem {

	private static final String[] ARROW_TYPES = {"arrow", "spectral_arrow", "tipped_arrow"};

	@Override
	public void registerSettings() {
		getProperties().addSetting("showAlways", false);
	}

	@Override
	protected ItemStack getStack(boolean dummy) {
		if (dummy) {
			return The5zigMod.getVars().getItemByName("minecraft:arrow", 10);
		}
		int arrowCount = 0;
		for (String arrowType : ARROW_TYPES) {
			arrowCount += The5zigMod.getVars().getItemCount("minecraft:" + arrowType);
		}
		ItemStack mainHand = The5zigMod.getVars().getItemInMainHand();
		ItemStack offHand = The5zigMod.getVars().getItemInOffHand();
		if (arrowCount > 0) {
			if ((Boolean) getProperties().getSetting("showAlways").get() || (mainHand != null && "minecraft:bow".equals(mainHand.getKey()))) {
				return The5zigMod.getVars().getItemByName("minecraft:arrow", arrowCount);
			}
			if ((Boolean) getProperties().getSetting("showAlways").get() || (offHand != null && "minecraft:bow".equals(offHand.getKey()))) {
				return The5zigMod.getVars().getItemByName("minecraft:arrow", arrowCount);
			}
		}
		return null;
	}
}
