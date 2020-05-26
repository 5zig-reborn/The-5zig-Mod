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

import com.google.common.collect.ImmutableMap;
import eu.the5zig.mod.I18n;
import eu.the5zig.mod.The5zigMod;

import java.util.Map;

public class TargetBlockCoordinates extends Coordinates {
	@Override
	public boolean shouldRender(boolean dummy) {
		return dummy || The5zigMod.getVars().hasTargetBlock();
	}

	@Override
	protected Map<String, String> getCoordinates(boolean dummy) {
		CoordStyle coordStyle = (CoordStyle) getProperties().getSetting("coordStyle").get();
		int xPos = dummy ? 0 : The5zigMod.getVars().getTargetBlockX();
		int yPos = dummy ? 64 : The5zigMod.getVars().getTargetBlockY();
		int zPos = dummy ? 0 : The5zigMod.getVars().getTargetBlockZ();
		if (coordStyle == CoordStyle.BELOW_OTHER) {
			String xPre = getPrefix(I18n.translate("ingame.target") + " X");
			String yPre = getPrefix(I18n.translate("ingame.target") + " Y") ;
			String zPre = getPrefix(I18n.translate("ingame.target") + " Z");
			return ImmutableMap.of(xPre, Integer.toString(xPos, 10), yPre, Integer.toString(yPos, 10), zPre, Integer.toString(zPos, 10));
		} else {
			String pre = getPrefix(I18n.translate("ingame.target") + " X/Y/Z");
			return ImmutableMap.of(pre, xPos + "/" + yPos + "/" + zPos);
		}
	}
}
