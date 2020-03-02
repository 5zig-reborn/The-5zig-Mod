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

package eu.the5zig.mod.render;

import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.gui.Gui;

import java.util.List;

public class RenderHelperImpl implements RenderHelper {

	@Override
	public void drawString(String string, int x, int y, Object... format) {
		The5zigMod.getVars().drawString(string, x, y, format);
	}

	@Override
	public void drawString(String string, int x, int y) {
		The5zigMod.getVars().drawString(string, x, y);
	}

	@Override
	public void drawCenteredString(String string, int x, int y) {
		The5zigMod.getVars().drawCenteredString(string, x, y);
	}

	@Override
	public void drawCenteredString(String string, int x, int y, int color) {
		The5zigMod.getVars().drawCenteredString(string, x, y, color);
	}

	@Override
	public void drawString(String string, int x, int y, int color, Object... format) {
		The5zigMod.getVars().drawString(string, x, y, color, format);
	}

	@Override
	public void drawString(String string, int x, int y, int color) {
		The5zigMod.getVars().drawString(string, x, y, color);
	}

	@Override
	public void drawString(String string, int x, int y, int color, boolean withShadow) {
		The5zigMod.getVars().drawString(string, x, y, color, withShadow);
	}

	@Override
	public List<String> splitStringToWidth(String string, int maxWidth) {
		return The5zigMod.getVars().splitStringToWidth(string, maxWidth);
	}

	@Override
	public int getStringWidth(String string) {
		return The5zigMod.getVars().getStringWidth(string);
	}

	@Override
	public String shortenToWidth(String string, int width) {
		return The5zigMod.getVars().shortenToWidth(string, width);
	}

	@Override
	public void drawRect(double left, double top, double right, double bottom, int color) {
		Gui.drawRect(left, top, right, bottom, color);
	}

	@Override
	public void drawGradientRect(double left, double top, double right, double bottom, int startColor, int endColor, boolean verticalGradient) {
		Gui.drawGradientRect(left, top, right, bottom, startColor, endColor, verticalGradient);
	}

	@Override
	public void drawRectOutline(int left, int top, int right, int bottom, int color) {
		Gui.drawRectOutline(left, top, right, bottom, color);
	}

	@Override
	public void drawRectInline(int left, int top, int right, int bottom, int color) {
		Gui.drawRectInline(left, top, right, bottom, color);
	}

	@Override
	public void drawScaledCenteredString(String string, int x, int y, float scale) {
		Gui.drawScaledCenteredString(string, x, y, scale);
	}

	@Override
	public void drawScaledString(String string, int x, int y, float scale) {
		Gui.drawScaledString(string, x, y, scale);
	}

	@Override
	public void drawLargeText(String string) {
		DisplayRenderer.largeTextRenderer.render(string);
	}
}
