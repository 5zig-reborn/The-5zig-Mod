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
import eu.the5zig.mod.gui.Gui;
import eu.the5zig.mod.modules.AbstractModuleItem;
import eu.the5zig.mod.render.RenderLocation;
import eu.the5zig.mod.util.GLUtil;
import eu.the5zig.mod.util.Vector2i;
import org.lwjgl.opengl.GL11;

public class CoordinatesClipboard extends AbstractModuleItem {

	@Override
	public void render(int x, int y, RenderLocation renderLocation, boolean dummy) {
		renderString(x, y, dummy, true);
		int width = getWidth(dummy);

		Vector2i coordinates = dummy ? new Vector2i(10, -53) : The5zigMod.getDataManager().getCoordinatesClipboard().getLocation();
		double dx = coordinates.getX() - (dummy ? 10 : The5zigMod.getVars().getPlayerPosX());
		double dz = coordinates.getY() - (dummy ? -10 : The5zigMod.getVars().getPlayerPosZ());

		// render direction arrow
		float yaw = (float) Math.atan2(dz, dx);
		float rotateAngle = (float) Math.toDegrees(yaw) - (dummy ? 0 : The5zigMod.getVars().getPlayerRotationYaw()) - 90;
		int x2 = x + width - 12;
		int y2 = y - 3;
		int xCentered = x2 + 6;
		int yCentered = y2 + 6;

		GLUtil.pushMatrix();
		GLUtil.translate(xCentered, yCentered, 0);
		GL11.glRotatef(rotateAngle, 0, 0, 1);
		GLUtil.translate(x2 - xCentered, y2 - yCentered, 0);
		The5zigMod.getVars().bindTexture(The5zigMod.ITEMS);
		Gui.drawModalRectWithCustomSizedTexture(0, 0, 72, 0, 12, 12, 96, 96);
		GLUtil.popMatrix();
	}

	@Override
	public int getWidth(boolean dummy) {
		return renderString(0, 0, dummy, false) + 16;
	}

	@Override
	public int getHeight(boolean dummy) {
		return 10;
	}

	@Override
	public boolean shouldRender(boolean dummy) {
		return dummy || (The5zigMod.getDataManager().getCoordinatesClipboard().getLocation() != null && !The5zigMod.getVars().isPlayerListShown());
	}

	private int renderString(int x, int y, boolean dummy, boolean render) {
		int width = The5zigMod.getVars().getStringWidth(getPrefix("X"));
		int totalWidth = width * 2;
		if (dummy) {
			String xPos = "10 ";
			String yPos = "-53 (105 m) ";
			if(render) {
				renderPrefix(getPrefix("X"), x, y);
				The5zigMod.getVars().drawString(xPos, x += width, y, getMainColor());
				renderPrefix(getPrefix("Z"), x += The5zigMod.getVars().getStringWidth(xPos), y);
				The5zigMod.getVars().drawString(yPos, x + width, y, getMainColor());
			}
			return totalWidth + The5zigMod.getVars().getStringWidth(xPos + yPos);
		}
		Vector2i coordinates = The5zigMod.getDataManager().getCoordinatesClipboard().getLocation();
		double dx = coordinates.getX() - The5zigMod.getVars().getPlayerPosX();
		double dz = coordinates.getY() - The5zigMod.getVars().getPlayerPosZ();
		// render distance
		double distance = Math.sqrt(dx * dx + dz * dz);
		String xPos = The5zigMod.getDataManager().getCoordinatesClipboard().getLocation().getX() + " ";
		String yPos = The5zigMod.getDataManager().getCoordinatesClipboard().getLocation().getY() + " (" + shorten(distance) + " m)";
		if(render) {
			renderPrefix(getPrefix("X"), x, y);
			The5zigMod.getVars().drawString(xPos, x += width, y, getMainColor());
			renderPrefix(getPrefix("Z"), x += The5zigMod.getVars().getStringWidth(xPos), y);
			The5zigMod.getVars().drawString(yPos, x + width, y, getMainColor());
		}
		return totalWidth + The5zigMod.getVars().getStringWidth(xPos + yPos);
	}
}
