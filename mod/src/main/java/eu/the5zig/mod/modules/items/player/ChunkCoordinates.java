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

import eu.the5zig.mod.I18n;
import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.modules.AbstractModuleItem;
import eu.the5zig.mod.render.RenderLocation;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ChunkCoordinates extends AbstractModuleItem {

	@Override
	public void registerSettings() {
		getProperties().addSetting("coordStyle", CoordStyle.BELOW_OTHER, CoordStyle.class);
		getProperties().addSetting("chunkStyle", ChunkStyle.CHUNK, ChunkStyle.class);
	}

	@Override
	public void render(int x, int y, RenderLocation renderLocation, boolean dummy) {
		List<String> coordinates = getCoordinates(dummy);
		for (int i = 0; i < coordinates.size(); i++) {
			draw(coordinates.get(i), x, y + 10 * i, renderLocation == RenderLocation.CENTERED);
		}
	}

	private List<String> getCoordinates(boolean dummy) {
		CoordStyle coordStyle = (CoordStyle) getProperties().getSetting("coordStyle").get();
		ChunkStyle chunkStyle = (ChunkStyle) getProperties().getSetting("chunkStyle").get();
		int xPos = dummy ? 100 : The5zigMod.getVars().getPlayerChunkX();
		int xPosRel = dummy ? 4 : The5zigMod.getVars().getPlayerChunkRelX();
		int yPos = dummy ? 64 : The5zigMod.getVars().getPlayerChunkY();
		int yPosRel = dummy ? 0 : The5zigMod.getVars().getPlayerChunkRelY();
		int zPos = dummy ? 100 : The5zigMod.getVars().getPlayerChunkZ();
		int zPosRel = dummy ? 4 : The5zigMod.getVars().getPlayerChunkRelZ();
		if (coordStyle == CoordStyle.BELOW_OTHER) {
			String xPre = getPrefix(I18n.translate("ingame.chunk") + " X");
			String yPre = getPrefix(I18n.translate("ingame.chunk") + " Y");
			String zPre = getPrefix(I18n.translate("ingame.chunk") + " Z");
			if (chunkStyle == ChunkStyle.CHUNK) {
				return Arrays.asList(xPre + xPos, yPre + yPos, zPre + zPos);
			} else if (chunkStyle == ChunkStyle.RELATIVE) {
				return Arrays.asList(xPre + xPosRel, yPre + yPosRel, zPre + zPosRel);
			} else {
				return Arrays.asList(xPre + xPos + " (" + xPosRel + ")", yPre + yPos + " (" + yPosRel + ")", zPre + zPos + " (" + zPosRel + ")");
			}
		} else {
			String pre = getPrefix(I18n.translate("ingame.chunk") + " X/Y/Z");
			if (chunkStyle == ChunkStyle.CHUNK) {
				return Collections.singletonList(pre + xPos + "/" + yPos + "/" + zPos);
			} else if (chunkStyle == ChunkStyle.RELATIVE) {
				return Collections.singletonList(pre + xPosRel + "/" + yPosRel + "/" + zPosRel);
			} else {
				return Collections.singletonList(pre + xPos + " (" + xPosRel + ")" + "/" + yPos + " (" + yPosRel + ")" + "/" + zPos + " (" + zPosRel + ")");
			}
		}
	}

	private void draw(String string, int x, int y, boolean centered) {
		if (centered) {
			The5zigMod.getVars().drawCenteredString(string, x + The5zigMod.getVars().getStringWidth(string) / 2, y);
		} else {
			The5zigMod.getVars().drawString(string, x, y);
		}
	}

	@Override
	public int getWidth(boolean dummy) {
		List<String> coordinates = getCoordinates(dummy);
		int maxWidth = 0;
		for (String coordinate : coordinates) {
			int width = The5zigMod.getVars().getStringWidth(coordinate);
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

	public enum ChunkStyle {
		RELATIVE, CHUNK, BOTH
	}
}
