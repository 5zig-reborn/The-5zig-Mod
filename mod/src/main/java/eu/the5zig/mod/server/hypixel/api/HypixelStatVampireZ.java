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

package eu.the5zig.mod.server.hypixel.api;

import com.google.gson.JsonObject;
import eu.the5zig.mod.server.hypixel.HypixelGameType;

import java.util.Arrays;
import java.util.List;

public class HypixelStatVampireZ extends HypixelStatCategory {

	public HypixelStatVampireZ() {
		super(HypixelGameType.VAMPIREZ);
	}

	@Override
	public List<String> getStats(JsonObject object) {
		return Arrays.asList(parseInt(object, "coins"), parseInt(object, "human_kills"), parseInt(object, "human_deaths"), parseInt(object, "human_wins"), parseInt(object, "vampire_kills"),
				parseInt(object, "vampire_deaths"), parseInt(object, "most_vampire_kills"), parseInt(object, "zombie_kills"));
	}
}
