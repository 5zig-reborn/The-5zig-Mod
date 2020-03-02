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
import eu.the5zig.mod.modules.StringItem;

public class DamageResistance extends StringItem {

	private float previousResistanceFactor;
	private long lastUpdated;

	@Override
	protected Object getValue(boolean dummy) {
		if (System.currentTimeMillis() - lastUpdated > 100) {
			lastUpdated = System.currentTimeMillis();
			previousResistanceFactor = 34.0f;
			if (!dummy) {
				previousResistanceFactor = The5zigMod.getVars().getResistanceFactor();
			}
		}
		return shorten(previousResistanceFactor) + "%";
	}

	@Override
	public String getTranslation() {
		return "ingame.damage_resistance";
	}
}
