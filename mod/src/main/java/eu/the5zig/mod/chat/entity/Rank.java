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

package eu.the5zig.mod.chat.entity;

import eu.the5zig.util.minecraft.ChatColor;

import java.util.ArrayList;

/**
 * Created by 5zig.
 * All rights reserved © 2015
 */
public enum Rank {

	DEVELOPER(3, '6', "Developer"),
	TRANSLATOR(2, 'e', "Translator"),
	MODERATOR(7, '3', "Moderator"),
	PATRON2(4, 'a', "Tier II Patron"),
	CAPE_CUSTOM(6, 'r', "Custom Cape"),
	PATRON(1, '2', "Tier I Patron"),
	CAPE_DEFAULT(5, 'r', "Default Cape"),
	BUG_REPORTER(9, 'd', "Bug Report Team"),
	PLUGIN_DEV(8, '3', "Plugin Developer"),
	USER(0, 'r', "User");

	private int index;
	private final char colorCode;
	private String display;

	Rank(int fixedOrdinal, char code, String display) {
		this.index = fixedOrdinal;
		this.colorCode = code;
		this.display = display;
	}

	public boolean isDefaultCape() {
		return ordinal() <= CAPE_DEFAULT.ordinal();
	}

	public boolean isCustomCape() {
		return ordinal() <= CAPE_CUSTOM.ordinal();
	}

	long getBit() {
		return 1 << index;
	}

	boolean isSet(long bits) {
		long bit = getBit();
		return (bits & bit) == bit;
	}

	public void addIfSet(long bits, ArrayList<Rank> roles) {
		if(isSet(bits))
			roles.add(this);
	}

	public String getDisplay() {
		return display;
	}

	public String getColorCode() {
		return new String(new char[]{ChatColor.COLOR_CHAR, colorCode});
	}

	public String getColoredDisplay() {
		return getColorCode() + getDisplay();
	}

	public static String buildList(ArrayList<Rank> ranks) {
		StringBuilder builder = new StringBuilder();
		for(Rank rank : ranks) {
			builder.append(rank.getColoredDisplay()).append("§r, ");
		}
		builder.deleteCharAt(builder.length() - 2);
		return builder.toString().trim();
	}

}