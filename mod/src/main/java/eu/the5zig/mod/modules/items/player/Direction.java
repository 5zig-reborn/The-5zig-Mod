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
import eu.the5zig.mod.modules.StringItem;
import eu.the5zig.mod.render.BracketsFormatting;
import eu.the5zig.mod.render.DisplayRenderer;
import eu.the5zig.mod.util.MathUtil;

public class Direction extends StringItem {

	@Override
	public void registerSettings() {
		getProperties().addSetting("directionStyle", Style.STRING, Style.class);
		getProperties().addSetting("showDirectionTowards", false);
		getProperties().addSetting("countAngleNorth", false);
	}

	@Override
	protected Object getValue(boolean dummy) {
		return getF(dummy);
	}

	private String getF(boolean dummy) {
		float rotationYaw = dummy ? 0 : The5zigMod.getVars().getPlayerRotationYaw();
		Style directionStyle = (Style) getProperties().getSetting("directionStyle").get();
		if (directionStyle == Style.DEGREE) {
			boolean angleNorth = (boolean) getProperties().getSetting("countAngleNorth").get();
			double angle = Math.abs(rotationYaw) % 360.0;
			return shorten(angleNorth ? MathUtil.angleSumDegrees(angle, 180) : angle) + "\u00b0";
		}
		float fDir = rotationYaw / 360 * 4;
		fDir = fDir % 4;
		if (fDir < 0) {
			fDir = Math.abs(-4 - fDir);
		}

		String result;
		if (fDir >= 3.75 && fDir <= 4.0 || fDir >= 0.0 && fDir <= 0.25) {
			String s = shorten(fDir);
			if (s.startsWith("4"))
				s = "0" + s.substring(1);
			result = toDirection( s, I18n.translate("ingame.f.south"), "Z+");
		} else if (fDir > 0.25 && fDir < 0.75) {
			result = toDirection(shorten(fDir), I18n.translate("ingame.f.south_west"), "X-, Z+");
		} else if (fDir >= 0.75 && fDir <= 1.25) {
			result = toDirection(shorten(fDir), I18n.translate("ingame.f.west"), "X-");
		} else if (fDir > 1.25 && fDir < 1.75) {
			result = toDirection(shorten(fDir), I18n.translate("ingame.f.north_west"), "X-, Z-");
		} else if (fDir >= 1.75 && fDir <= 2.25) {
			result = toDirection(shorten(fDir), I18n.translate("ingame.f.north"), "Z-");
		} else if (fDir > 2.25 && fDir < 2.75) {
			result = toDirection(shorten(fDir), I18n.translate("ingame.f.north_east"), "X+, " + "Z-");
		} else if (fDir >= 2.75 && fDir <= 3.25) {
			result = toDirection(shorten(fDir), I18n.translate("ingame.f.east"), "X+");
		} else if (fDir > 3.25 && fDir < 3.75) {
			String s = shorten(fDir);
			if (s.startsWith("4"))
				s = "0" + s.substring(1);
			result = toDirection(s, I18n.translate("ingame.f.south_east"), "X+, Z+");
		} else {
			result = I18n.translate("error");
		}
		return result;
	}

	private String toDirection(String number, String direction, String towards) {
		DisplayRenderer renderer = The5zigMod.getRenderer();
		Style directionStyle = (Style) getProperties().getSetting("directionStyle").get();
		String rightBr = The5zigMod.getConfig().getEnum("formattingBrackets", BracketsFormatting.class).hasFirst() ? renderer.getBracketsRight() : "";
		towards = " " + renderer.getBrackets() + renderer.getBracketsLeft() + renderer.getPrefix() + towards + renderer.getBrackets() + rightBr;

		boolean directionTowards = (Boolean) getProperties().getSetting("showDirectionTowards").get();
		if (directionStyle == Style.NUMBER)
			return number + (directionTowards ? towards : "");
		if (directionStyle == Style.STRING)
			return direction + (directionTowards ? towards : "");
		return direction + " (" + number + ") " + (directionTowards ? towards : "");
	}

	@Override
	public String getName() {
		return "F";
	}

	public enum Style {

		STRING, NUMBER, BOTH, DEGREE

	}
}
