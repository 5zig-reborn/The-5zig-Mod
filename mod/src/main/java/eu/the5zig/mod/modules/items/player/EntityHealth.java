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
import eu.the5zig.mod.modules.AbstractModuleItem;
import eu.the5zig.mod.render.RenderLocation;
import eu.the5zig.util.minecraft.ChatColor;

public class EntityHealth extends AbstractModuleItem {

	private static final float SCALE = 0.65f;

	@Override
	public void render(int x, int y, RenderLocation renderLocation, boolean dummy) {
		String string = getString(dummy);
		The5zigMod.getDataManager().getCrossHairDistanceListener().drawString(string, x, y, false, SCALE, false);
	}

	@Override
	public int getWidth(boolean dummy) {
		return (int) (The5zigMod.getVars().getStringWidth(getString(dummy)) * SCALE);
	}

	@Override
	public int getHeight(boolean dummy) {
		return (int) (10 * SCALE);
	}

	@Override
	public boolean shouldRender(boolean dummy) {
		return dummy || (The5zigMod.getDataManager().getCrossHairDistanceListener().getPointedEntity() != null && (The5zigMod.getDataManager().getServer() == null));
	}

	private String getString(boolean dummy) {
		return ChatColor.DARK_RED.toString() + (Math.ceil(getHealth(dummy)) / 2.0)+ " \u2764";
	}

	private float getHealth(boolean dummy) {
		return dummy ? 10 : The5zigMod.getVars().getHealth(The5zigMod.getDataManager().getCrossHairDistanceListener().getPointedEntity());
	}
}
