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

public class HypixelStatArcade extends HypixelStatCategory {

	public HypixelStatArcade() {
		super(HypixelGameType.ARCADE);
	}

	@Override
	public List<String> getStats(JsonObject object) {
		return Arrays.asList(parseInt(object, "coins"), parseInt(object, "kills_dayone"), parseInt(object, "wins_dayone"), parseInt(object, "headshots_dayone"),
				parseInt(object, "kills_dragonwars2"), parseInt(object, "kills_oneinthequiver"), parseInt(object, "deaths_oneinthequiver"), parseInt(object, "wins_oneinthequiver"),
				parseInt(object, "bounty_kills_oneinthequiver"), parseInt(object, "kills_throw_out"), parseInt(object, "deaths_throw_out"), parseInt(object, "poop_collected"),
				parseInt(object, "wins_party"), parseInt(object, "wins_party2"), parseInt(object, "sw_kills"), parseInt(object, "sw_empire_kills"), parseInt(object, "sw_rebel_kills"),
				parseInt(object, "sw_deaths"), parseInt(object, "sw_shots_fired"), parseInt(object, "sw_weekly_kills_a"), parseInt(object, "sw_monthly_kills_a"),
				parseInt(object, "wins_farm_hunt"));
	}

}
