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

import com.google.common.collect.ImmutableList;
import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.modules.AbstractModuleItem;
import eu.the5zig.mod.render.RenderLocation;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;

public class Coordinates extends AbstractModuleItem {

	@Override
	public void registerSettings() {
		getProperties().addSetting("coordStyle", CoordStyle.BELOW_OTHER, CoordStyle.class);
	}

	@Override
	public void render(int x, int y, RenderLocation renderLocation, boolean dummy) {
		List<Map.Entry<String, String>> coordinates = getCoordinates(dummy);
		int i = 0;
		for (Map.Entry<String, String> entry : coordinates) {
			draw(entry, x, y + 10 * i, renderLocation == RenderLocation.CENTERED);
			i++;
		}
	}

	protected List<Map.Entry<String, String>> getCoordinates(boolean dummy) {
		CoordStyle coordStyle = (CoordStyle) getProperties().getSetting("coordStyle").get();
		String xPos = shorten(dummy ? 0 : The5zigMod.getVars().getPlayerPosX());
		String yPos = shorten(dummy ? 64 : The5zigMod.getVars().getPlayerPosY());
		String zPos = shorten(dummy ? 0 : The5zigMod.getVars().getPlayerPosZ());
		if (coordStyle == CoordStyle.BELOW_OTHER) {
			return ImmutableList.of(pair(getPrefix("X"), xPos), pair(getPrefix("Y"), yPos), pair(getPrefix("Z"), zPos));
		} else {
			return ImmutableList.of(pair(getPrefix("X/Y/Z"), xPos + "/" + yPos + "/" + zPos));
		}
	}

	public static Map.Entry<String, String> pair(String key, String value) {
		return new AbstractMap.SimpleImmutableEntry<>(key, value);
	}

	private void draw(Map.Entry<String, String> pair, int x, int y, boolean centered) {
		if (centered) {
			int length = The5zigMod.getVars().getStringWidth(pair.getKey());
			renderPrefix(pair.getKey(), (x - length / 2), y);
			The5zigMod.getVars().drawCenteredString(pair.getValue(), x + length / 2, y, getMainColor());
		} else {
			renderPrefix(pair.getKey(), x, y);
			The5zigMod.getVars().drawString(formatValue(pair.getValue()), x + The5zigMod.getVars().getStringWidth(pair.getKey()), y, getMainColor());
		}
	}

	@Override
	public int getWidth(boolean dummy) {
		List<Map.Entry<String, String>> coordinates = getCoordinates(dummy);
		int maxWidth = 0;
		for (Map.Entry<String, String> pair : coordinates) {
			int width = The5zigMod.getVars().getStringWidth(pair.getKey() + pair.getValue());
			if (width > maxWidth) {
				maxWidth = width;
			}
		}
		return maxWidth;
	}

	@Override
	public int getHeight(boolean dummy) {
		return getProperties().getSetting("coordStyle").get() == CoordStyle.BELOW_OTHER ? 30 : 10;
	}

	public enum CoordStyle {

		BELOW_OTHER, SIDE_BY_SIDE

	}
}
