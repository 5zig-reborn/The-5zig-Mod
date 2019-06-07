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

public class HypixelStatTurboKartRacers extends HypixelStatCategory {

	public HypixelStatTurboKartRacers() {
		super(HypixelGameType.TURBO_KART_RACERS);
	}

	@Override
	public List<String> getStats(JsonObject object) {
		return Arrays.asList(parseInt(object, "coins"), parseInt(object, "wins"), parseInt(object, "gold_trophy"), parseInt(object, "silver_trophy"), parseInt(object, "bronze_trophy"),
				parseInt(object, "laps_completed"), parseInt(object, "coins_picked_up"), parseInt(object, "box_pickups"), parseInt(object, "banana_hits_sent"),
				parseInt(object, "banana_hits_received"));
	}
}
