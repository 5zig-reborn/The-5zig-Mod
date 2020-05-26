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

package eu.the5zig.mod.modules.items.server.simplehg;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import eu.the5zig.mod.I18n;
import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.modules.GameModeItem;
import eu.the5zig.mod.render.RenderLocation;
import eu.the5zig.mod.server.GameState;
import eu.the5zig.mod.server.simplehg.ServerSimpleHG;

import java.util.Iterator;
import java.util.Map;

public class SimpleHGMiniFeast extends GameModeItem<ServerSimpleHG.SimpleHG> {

	public SimpleHGMiniFeast() {
		super(ServerSimpleHG.SimpleHG.class, GameState.PREGAME, GameState.GAME);
	}

	@Override
	public void render(int x, int y, RenderLocation renderLocation, boolean dummy) {
		renderPrefix(getPrefix(I18n.translate("ingame.minifeast")), x, y);
		y += The5zigMod.getVars().getFontHeight();
		for (Map.Entry<String, String> line : getRenderList(dummy).entries()) {
			renderPrefix(line.getKey(), x, y);
			The5zigMod.getVars().drawString(line.getValue(), x + The5zigMod.getVars().getStringWidth(line.getKey()), y, getMainColor());
			y += The5zigMod.getVars().getFontHeight();
		}
	}

	@Override
	public int getWidth(boolean dummy) {
		int maxWidth = 0;
		for (Map.Entry<String, String> friend : getRenderList(dummy).entries()) {
			int width = The5zigMod.getVars().getStringWidth(friend.getKey() + friend.getValue());
			if (width > maxWidth) {
				maxWidth = width;
			}
		}
		return maxWidth;
	}

	@Override
	public int getHeight(boolean dummy) {
		return (getRenderList(dummy).size() + 1) * The5zigMod.getVars().getFontHeight();
	}

	private Multimap<String, String> getRenderList(boolean dummy) {
		String pre = getProperties().buildPrefix("");
		if (dummy) {
			Multimap<String, String> result = ArrayListMultimap.create();
			result.put(pre, "X: 0 - 100 Z: 0 - -100");
			result.put(pre, "X: 200 - 300 Z: 200 - 100");
			return result;
		}
		Multimap<String, String> result = ArrayListMultimap.create();
		for (Iterator<ServerSimpleHG.MiniFeast> iterator = getGameMode().getMiniFeasts().iterator(); iterator.hasNext(); ) {
			ServerSimpleHG.MiniFeast miniFeast = iterator.next();
			if (System.currentTimeMillis() - miniFeast.getTime() > 1000 * 60 * 5) {
				iterator.remove();
				continue;
			}
			result.put(pre, "X: " + miniFeast.getStart().getX() + " - " + miniFeast.getEnd().getX() + " Z: " + miniFeast.getStart().getY() + " - " + miniFeast.getEnd().getY());
		}
		return result;
	}

	@Override
	protected Object getValue(boolean dummy) {
		return getRenderList(dummy).isEmpty() ? null : 0;
	}
}
