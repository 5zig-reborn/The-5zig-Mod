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
import eu.the5zig.mod.event.EventHandler;
import eu.the5zig.mod.event.TickEvent;
import eu.the5zig.mod.util.GLUtil;
import eu.the5zig.mod.util.IVariables;
import eu.the5zig.util.Utils;
import org.lwjgl.opengl.GL11;

public class CrossHairDistanceListener {

	private double distance;
	private Object pointedEntity;
	private long lastPointedEntity;

	public CrossHairDistanceListener() {
		// The5zigMod.getListener().registerListener(this);
	}

	@EventHandler
	public void onTick(TickEvent event) {
		float maxDistance = The5zigMod.getConfig().getFloat("crosshairDistance");
		boolean showEntityHealth = (The5zigMod.getDataManager().getServer() == null) && The5zigMod.getModuleMaster()
				.isItemActive("ENTITY_HEALTH");
		if (pointedEntity != null && System.currentTimeMillis() - lastPointedEntity > 500) {
			pointedEntity = null;
			lastPointedEntity = 0;
		}
	}

	public void render() {
		if (The5zigMod.getConfig().getBool("showMod") && distance != -1) {
			drawString(Utils.getShortenedDouble(distance, The5zigMod.getConfig().getInt("numberPrecision")) + "m", The5zigMod.getVars().getScaledWidth() / 2,
					The5zigMod.getVars().getScaledHeight() / 2 + 8, true, .5f, true);
		}
	}

	public void drawString(String string, int x, int y, boolean alpha, float scale, boolean centered) {
		if (alpha) {
			GLUtil.enableBlend();
			GLUtil.tryBlendFuncSeparate(GL11.GL_ONE_MINUS_DST_COLOR, GL11.GL_ONE_MINUS_SRC_COLOR, 1, 0);
			GL11.glEnable(GL11.GL_ALPHA_TEST);
		}
		GLUtil.color(1, 1, 1, 1);
		GLUtil.pushMatrix();
		if (centered) {
			GLUtil.translate(x - The5zigMod.getVars().getStringWidth(string) / 2 * scale, y, 1);
		} else {
			GLUtil.translate(x, y, 1);
		}
		GLUtil.scale(scale, scale, scale);
		The5zigMod.getVars().drawString(string, 0, 0, 0xffffff, false);
		GLUtil.popMatrix();
		if (alpha) {
			GLUtil.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
			GLUtil.disableBlend();
		}
	}

	public Object getPointedEntity() {
		return pointedEntity;
	}
}
