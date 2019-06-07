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

package eu.the5zig.mod.chat.entity;

import eu.the5zig.util.minecraft.ChatColor;

/**
 * Created by 5zig.
 * All rights reserved © 2015
 */
public enum Rank {

	/**
	 * No color :C
	 */
	NONE('r'),
	/**
	 * Light green color
	 */
	DEFAULT('a'),
	/**
	 * Gold color
	 */
	CUSTOM('6'),
	/**
	 * Red color
	 */
	SPECIAL('5');

	private final char colorCode;

	Rank(char colorCode) {
		this.colorCode = colorCode;
	}

	public String getColorCode() {
		return new String(new char[]{ChatColor.COLOR_CHAR, colorCode});
	}

}