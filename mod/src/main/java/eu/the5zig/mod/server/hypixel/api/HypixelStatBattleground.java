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

package eu.the5zig.mod.server.hypixel.api;

import com.google.gson.JsonObject;
import eu.the5zig.mod.server.hypixel.HypixelGameType;

import java.util.Arrays;
import java.util.List;

public class HypixelStatBattleground extends HypixelStatCategory {

	public HypixelStatBattleground() {
		super(HypixelGameType.BATTLEGROUND);
	}

	@Override
	public List<String> getStats(JsonObject object) {
		return Arrays.asList(parseInt(object, "coins"), parseString(object, "chosen_class"), parseInt(object, "kills"), parseInt(object, "assists"), parseInt(object, "deaths"),
				parseInt(object, "wins"), parseInt(object, "wins_domination"), parseInt(object, "wins_capturetheflag"), parseInt(object, "losses"), parseInt(object, "damage"),
				parseInt(object, "repaired"), parseInt(object, "flag_conquer_self"));
	}
}
