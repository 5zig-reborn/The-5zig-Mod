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

import eu.the5zig.mod.I18n;
import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.manager.DeathLocation;
import eu.the5zig.mod.manager.WorldType;
import eu.the5zig.mod.modules.StringItem;

public class DeathCoordinates extends StringItem {

	@Override
	protected Object getValue(boolean dummy) {
		if (dummy) {
			return shorten(10) + "/" + shorten(64) + "/" + shorten(10);
		}
		DeathLocation location = The5zigMod.getDataManager().getDeathLocation();
		String x = shorten(location.getCoordinates().getX());
		String y = shorten(location.getCoordinates().getY());
		String z = shorten(location.getCoordinates().getZ());
		String result = x + "/" + y + "/" + z;
		if (location.getWorldType() != WorldType.OVERWORLD) {
			result += "(" + I18n.translate("modules.item.death_coordinates." + location.getWorldType().toString().toLowerCase()) + ")";
		}
		return result;
	}

	@Override
	public boolean shouldRender(boolean dummy) {
		return dummy || The5zigMod.getDataManager().getDeathLocation() != null;
	}

	@Override
	public String getTranslation() {
		return "ingame.death_coordinates";
	}
}
